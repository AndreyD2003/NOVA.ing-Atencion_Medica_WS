# DTOs y contratos

### DTOs explícitos en las fuentes

* `GrafoClinicoDto`
* `NaturalLanguageQueryRequest`
* `NaturalLanguageQueryResponse`
* `CitaDetalleRemoteDto`
* DTOs remotos como `PacienteRemoteDto` y `CitaRemoteDto`

### Cómo se encadenan

```
DTO remoto
→ DTO semántico
→ modelo RDF
→ respuesta RDF o resultado SPARQL
```

### Qué devuelve la consulta en lenguaje natural

La respuesta incluye:

* SPARQL generado,
* resultados tabulares,
* mensaje de estado.
