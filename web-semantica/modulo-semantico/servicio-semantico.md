# Servicio semántico

`msvc-web-semantica` construye una representación semántica del dominio clínico y habilita consultas avanzadas.

### Qué hace

* genera grafo clínico por cita,
* genera grafo global del sistema,
* serializa RDF,
* ejecuta SPARQL,
* traduce preguntas en lenguaje natural a SPARQL.

### Componentes funcionales

{% stepper %}
{% step %}
### `SemanticGraphController`

Expone endpoints para grafo y SPARQL.
{% endstep %}

{% step %}
### `NaturalLanguageQueryController`

Recibe preguntas en lenguaje natural.
{% endstep %}

{% step %}
### `SemanticGraphServiceImpl`

Construye `GrafoClinicoDto` desde `CitaDetalleRemoteDto`.
{% endstep %}

{% step %}
### `RdfGraphServiceImpl`

Construye el modelo Jena, aplica razonamiento OWL y serializa.
{% endstep %}

{% step %}
### `SparqlQueryServiceImpl`

Ejecuta consultas `SELECT` sobre modelos RDF.
{% endstep %}

{% step %}
### `NaturalLanguageQueryServiceImpl`

Detecta intenciones y genera SPARQL por patrones.
{% endstep %}
{% endstepper %}

### Dependencias externas

Clientes Feign:

* `PacienteClientRest` → `http://localhost:8083/pacientes`
* `MedicoClientRest` → `http://localhost:8080/medicos`
* `CitaClientRest` → `http://localhost:8081/citas`
* `DiagnosticoClientRest` → `http://localhost:8082/diagnosticos`

### Namespaces

* Ontología: `http://nova.ing/ontology/`
* Recursos: `http://nova.ing/atencion-medica/`

### Consideraciones de inferencia

Se añaden axiomas de subclase y dominio/rango para potenciar las consultas con razonador OWL de Jena.

### Límites actuales

* No hay triplestore persistente.
* El grafo se construye bajo demanda.
* Fallos de integración afectan la completitud del grafo.
