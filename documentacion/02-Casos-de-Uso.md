# Casos de Uso y Flujo del Código

## Objetivo

Este documento explica cómo funciona el código del proyecto desde la ejecución real de los controladores y servicios, con foco en casos de uso y secuencias completas.

## Cómo funciona el sistema

- El sistema está dividido en microservicios: `msvc-paciente`, `msvc-medico`, `msvc-cita`, `msvc-diagnostico` y `msvc-web-semantica`.
- El núcleo operativo es `msvc-cita`, porque valida agenda, crea citas y arma detalle clínico.
- `msvc-web-semantica` transforma datos clínicos a RDF y permite consulta por SPARQL o lenguaje natural.

## Caso de uso 1: Crear cita médica

### Flujo funcional

1. El cliente envía `POST /citas`.
2. `CitaController` recibe y delega a `CitaServiceImpl.guardar`.
3. El servicio normaliza fecha.
4. Valida conflictos de horario para médico y paciente.
5. Consulta remoto a `msvc-medico` y `msvc-paciente` para confirmar estado `ACTIVO`.
6. Si todo es válido, guarda en BD y responde `201`.

### Secuencia completa

```mermaid
sequenceDiagram
  autonumber
  participant U as Usuario/Frontend
  participant CCtrl as CitaController
  participant CSrv as CitaServiceImpl
  participant CRepo as CitaRepository
  participant MCl as MedicoClientRest
  participant PCl as PacienteClientRest
  participant DB as BD Citas

  U->>CCtrl: POST /citas
  CCtrl->>CSrv: guardar(cita)
  CSrv->>CSrv: normalizar fechaCita
  CSrv->>CRepo: existsConflictMedico(...)
  CRepo-->>CSrv: resultado
  CSrv->>CRepo: existsConflictPaciente(...)
  CRepo-->>CSrv: resultado
  alt hay conflicto
    CSrv-->>CCtrl: RuntimeException
    CCtrl-->>U: 400 Bad Request
  else sin conflicto
    CSrv->>MCl: detalle(medicoId)
    MCl-->>CSrv: Medico
    CSrv->>PCl: detalle(pacienteId)
    PCl-->>CSrv: Paciente
    alt médico/paciente inválido
      CSrv-->>CCtrl: RuntimeException
      CCtrl-->>U: 400 Bad Request
    else válido
      CSrv->>CRepo: save(cita)
      CRepo->>DB: persistir
      DB-->>CRepo: OK
      CRepo-->>CSrv: entidad guardada
      CSrv-->>CCtrl: entidad guardada
      CCtrl-->>U: 201 Created
    end
  end
```

## Caso de uso 2: Ver cita con detalle clínico

### Flujo funcional

1. El cliente consulta `GET /citas/con-detalle/{id}`.
2. `CitaServiceImpl.porIdConDetalle` obtiene cita local.
3. Enriquecimiento remoto:
   - paciente por `PacienteClientRest`,
   - médico por `MedicoClientRest`,
   - diagnósticos por `DiagnosticoClientRest`.
4. Devuelve un DTO agregado con toda la información disponible.

### Secuencia completa

```mermaid
sequenceDiagram
  autonumber
  participant U as Usuario/Frontend
  participant CCtrl as CitaController
  participant CSrv as CitaServiceImpl
  participant CRepo as CitaRepository
  participant PCl as PacienteClientRest
  participant MCl as MedicoClientRest
  participant DCl as DiagnosticoClientRest

  U->>CCtrl: GET /citas/con-detalle/{id}
  CCtrl->>CSrv: porIdConDetalle(id)
  CSrv->>CRepo: findById(id)
  CRepo-->>CSrv: Optional<CitaEntity>
  alt cita no existe
    CSrv-->>CCtrl: Optional.empty
    CCtrl-->>U: 404 Not Found
  else existe
    CSrv->>PCl: detalle(pacienteId)
    PCl-->>CSrv: Paciente
    CSrv->>MCl: detalle(medicoId)
    MCl-->>CSrv: Medico
    CSrv->>DCl: listarPorCita(citaId)
    DCl-->>CSrv: Diagnosticos[]
    CSrv-->>CCtrl: CitaDetalle
    CCtrl-->>U: 200 OK
  end
```

## Caso de uso 3: Generar RDF por cita

### Flujo funcional

1. El cliente llama `GET /semantic/grafo/cita/{id}/rdf`.
2. `SemanticGraphController` delega a `RdfGraphServiceImpl`.
3. `RdfGraphServiceImpl` pide el grafo clínico a `SemanticGraphServiceImpl`.
4. `SemanticGraphServiceImpl` consume `msvc-cita` con detalle agregado.
5. Se mapea a DTO semántico.
6. Se convierte a modelo RDF (Jena).
7. Se aplica razonamiento OWL.
8. Se serializa en Turtle/RDFXML/JSON-LD.

### Secuencia completa

```mermaid
sequenceDiagram
  autonumber
  participant U as Usuario/Frontend
  participant SGC as SemanticGraphController
  participant RGS as RdfGraphServiceImpl
  participant SGS as SemanticGraphServiceImpl
  participant CCl as CitaClientRest
  participant RMB as RdfModelBuilder
  participant Jena as Jena RDF/OWL

  U->>SGC: GET /semantic/grafo/cita/{id}/rdf?formato=TURTLE
  SGC->>RGS: serializarModeloPorCitaId(id, formato)
  RGS->>RGS: construirModeloPorCitaId(id)
  RGS->>SGS: construirGrafoPorCitaId(id)
  SGS->>CCl: obtenerCitaConDetalle(id)
  CCl-->>SGS: CitaDetalleRemoteDto
  SGS-->>RGS: GrafoClinicoDto
  RGS->>RMB: fromGrafoClinico(grafo)
  RMB-->>RGS: Model
  RGS->>Jena: aplicarRazonamientoOwl(model)
  Jena-->>RGS: InfModel
  RGS->>Jena: serializar(formato)
  Jena-->>RGS: RDF texto
  RGS-->>SGC: RDF texto
  SGC-->>U: 200 + Content-Type semántico
```

## Caso de uso 4: Consulta en lenguaje natural

### Flujo funcional

1. El cliente envía pregunta a `POST /semantic/nl/query`.
2. `NaturalLanguageQueryServiceImpl`:
   - normaliza texto,
   - detecta intención,
   - construye SPARQL.
3. Ejecuta SPARQL sobre modelo completo vía `SparqlQueryServiceImpl`.
4. Devuelve respuesta con pregunta, SPARQL generado y resultados.

### Secuencia completa

```mermaid
sequenceDiagram
  autonumber
  participant U as Usuario/Frontend
  participant NLC as NaturalLanguageQueryController
  participant NLS as NaturalLanguageQueryServiceImpl
  participant SQS as SparqlQueryServiceImpl
  participant RGS as RdfGraphServiceImpl
  participant Pac as PacienteClientRest
  participant Med as MedicoClientRest
  participant Cit as CitaClientRest
  participant Dia as DiagnosticoClientRest
  participant Jena as Jena SPARQL

  U->>NLC: POST /semantic/nl/query {"pregunta":"lista medicos activos"}
  NLC->>NLS: ejecutarConsulta(request)
  NLS->>NLS: normalizar + detectarIntent + construirSparql
  NLS->>SQS: ejecutarSelectSistemaCompleto(sparql)
  SQS->>RGS: construirModeloSistemaCompleto()
  RGS->>Pac: listar()
  Pac-->>RGS: pacientes[]
  RGS->>Med: listar()
  Med-->>RGS: medicos[]
  RGS->>Cit: listarTodas()
  Cit-->>RGS: citas[]
  RGS->>Dia: listar()
  Dia-->>RGS: diagnosticos[]
  RGS-->>SQS: Model RDF inferido
  SQS->>Jena: execSelect(sparql)
  Jena-->>SQS: ResultSet
  SQS-->>NLS: resultados tabulares
  NLS-->>NLC: NaturalLanguageQueryResponse
  NLC-->>U: 200 OK
```

## Mapa rápido de clases involucradas

- Citas:
  - `CitaController`
  - `CitaServiceImpl`
  - `CitaRepository`
  - `PacienteClientRest`, `MedicoClientRest`, `DiagnosticoClientRest`
- Web semántica:
  - `SemanticGraphController`
  - `NaturalLanguageQueryController`
  - `SemanticGraphServiceImpl`
  - `RdfGraphServiceImpl`
  - `SparqlQueryServiceImpl`
  - `NaturalLanguageQueryServiceImpl`
  - `RdfModelBuilder`
