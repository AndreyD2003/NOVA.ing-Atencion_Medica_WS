# MSVC Web Semántica — Documentación Técnica

## Propósito

Construir una representación semántica del dominio clínico y habilitar consultas avanzadas:

- generación de grafo clínico por cita,
- generación de grafo global del sistema,
- serialización RDF (Turtle, RDF/XML, JSON-LD),
- ejecución de SPARQL,
- consulta en lenguaje natural traducida a SPARQL.

## Componentes funcionales

### 1) `SemanticGraphController`

- Endpoints REST de grafo y SPARQL.
- Entrada: `id` de cita, `formato`, consulta SPARQL.
- Salida: DTO semántico, texto RDF o lista tabular de resultados.

### 2) `NaturalLanguageQueryController`

- Endpoint para preguntas en lenguaje natural.
- Entrada: `NaturalLanguageQueryRequest { pregunta }`.
- Salida: `NaturalLanguageQueryResponse` con SPARQL generado y resultados.

### 3) `SemanticGraphServiceImpl`

- Construye `GrafoClinicoDto` a partir de `CitaDetalleRemoteDto`.
- Mapea entidades remotas a DTOs semánticos con IRIs estables.

### 4) `RdfGraphServiceImpl`

- Construye modelo Jena por cita y del sistema completo.
- Aplica razonamiento OWL para inferencias de clases/propiedades.
- Serializa en diferentes formatos RDF.

### 5) `SparqlQueryServiceImpl`

- Ejecuta `SELECT` sobre modelos RDF.
- Normaliza valores de salida para ontología y base IRI.

### 6) `NaturalLanguageQueryServiceImpl`

- Detecta intenciones por patrones de texto.
- Traduce preguntas a SPARQL parametrizado.
- Soporta filtros por id, nombre, especialidad y día.

## Endpoints

- `GET /semantic/grafo/cita/{id}`
- `GET /semantic/grafo/cita/{id}/rdf?formato=TURTLE|RDFXML|JSONLD`
- `POST /semantic/grafo/cita/{id}/sparql`
- `GET /semantic/grafo/sistema/rdf?formato=TURTLE|RDFXML|JSONLD`
- `POST /semantic/grafo/sistema/sparql`
- `POST /semantic/nl/query`

## Dependencias externas

Clientes Feign:

- `PacienteClientRest` (`http://localhost:8083/pacientes`)
- `MedicoClientRest` (`http://localhost:8080/medicos`)
- `CitaClientRest` (`http://localhost:8081/citas`)
- `DiagnosticoClientRest` (`http://localhost:8082/diagnosticos`)

## Modelo semántico

### Namespaces

- Ontología: `http://nova.ing/ontology/`
- Recursos: `http://nova.ing/atencion-medica/`

### Clases principales usadas

- `Paciente`
- `Medico`
- `Cita`
- `Diagnostico`
- `Horario`
- clases auxiliares de estado y tipo (`EstadoPaciente_*`, `EstadoCita_*`, `TipoDiagnostico_*`)

### Consideraciones de inferencia

Se añaden axiomas de subclase y dominio/rango para potenciar consultas con razonador OWL de Jena.

## Ejemplos de implementación

### Obtener RDF del sistema completo

```http
GET /semantic/grafo/sistema/rdf?formato=TURTLE
```

### Ejecutar SPARQL directo

```sparql
PREFIX onto: <http://nova.ing/ontology/>
SELECT ?cita ?paciente ?medico
WHERE {
  ?cita a onto:Cita ;
        onto:paciente ?paciente ;
        onto:medico ?medico .
}
```

```http
POST /semantic/grafo/sistema/sparql
Body: (consulta SPARQL como texto plano)
```

### Consulta en lenguaje natural

```json
POST /semantic/nl/query
{
  "pregunta": "lista citas del medico 2"
}
```

## Casos de uso

- Explotación analítica de datos clínicos en formato grafo.
- Integración con herramientas semánticas externas.
- Consultas de soporte a decisión para escenarios académicos.
- Generación de capas interoperables para investigación.

## Consideraciones importantes

- Las preguntas en lenguaje natural están basadas en patrones, no en modelos LLM.
- La traducción NL→SPARQL depende del vocabulario y plantillas implementadas.
- No hay endpoint público de escritura RDF persistente; los grafos se generan en tiempo de consulta.
- Errores de integración en servicios remotos pueden afectar la completitud del grafo.
