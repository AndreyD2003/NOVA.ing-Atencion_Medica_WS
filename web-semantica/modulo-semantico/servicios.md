# Servicios

### Servicios principales

#### `SemanticGraphServiceImpl`

* construye `GrafoClinicoDto` a partir de `CitaDetalleRemoteDto`,
* mapea datos remotos a DTOs semánticos,
* genera IRIs estables.

#### `RdfGraphServiceImpl`

* construye modelo Jena por cita y del sistema completo,
* aplica razonamiento OWL,
* serializa a Turtle, RDF/XML y JSON-LD.

#### `SparqlQueryServiceImpl`

* ejecuta consultas `SELECT` sobre modelos RDF,
* normaliza valores de salida para ontología y base IRI.

#### `NaturalLanguageQueryServiceImpl`

* normaliza texto,
* detecta intención,
* construye SPARQL,
* soporta filtros por id, nombre, especialidad y día,
* ejecuta la consulta sobre el modelo del sistema completo.

### Alcance actual

* el procesamiento de lenguaje natural es rule-based,
* la traducción NL→SPARQL depende de patrones y plantillas implementadas.
