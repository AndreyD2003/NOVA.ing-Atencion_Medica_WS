# Controladores

### `SemanticGraphController`

Expone endpoints REST de grafo y SPARQL.

#### Entradas

* `id` de cita,
* `formato`,
* consulta SPARQL.

#### Salidas

* DTO semántico,
* texto RDF,
* lista tabular de resultados.

### `NaturalLanguageQueryController`

Expone el endpoint para preguntas en lenguaje natural.

#### Entrada

* `NaturalLanguageQueryRequest { pregunta }`

#### Salida

* `NaturalLanguageQueryResponse` con SPARQL generado y resultados.
