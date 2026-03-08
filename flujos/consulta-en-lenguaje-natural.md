# Consulta en lenguaje natural

Este flujo permite consultar el sistema semántico sin escribir SPARQL manualmente.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente envía `POST /semantic/nl/query`
{% endstep %}

{% step %}
### `NaturalLanguageQueryServiceImpl` procesa la pregunta

Normaliza el texto, detecta la intención y construye SPARQL.
{% endstep %}

{% step %}
### `SparqlQueryServiceImpl` ejecuta la consulta

La consulta se corre sobre el modelo del sistema completo.
{% endstep %}

{% step %}
### Se devuelve la respuesta final

Incluye la pregunta original, el SPARQL generado y los resultados.
{% endstep %}
{% endstepper %}

### Secuencia técnica

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

### Alcance actual

* El procesamiento es rule-based.
* La traducción depende de patrones y vocabulario implementados.
* No usa modelos LLM.
