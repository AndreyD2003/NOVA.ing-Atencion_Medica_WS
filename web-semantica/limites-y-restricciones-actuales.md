# Límites y restricciones actuales

### Límites documentados

* el procesamiento de lenguaje natural es rule-based,
* no hay triplestore persistente,
* el grafo se construye bajo demanda,
* no hay endpoint público de escritura RDF persistente,
* errores de conectividad entre microservicios impactan los resultados semánticos,
* fallos de integración pueden afectar la completitud del grafo.

### Qué implica en la práctica

* las consultas dependen del estado de los servicios operacionales,
* la traducción NL→SPARQL depende del vocabulario implementado,
* el módulo está orientado a escenarios académicos y técnicos.
