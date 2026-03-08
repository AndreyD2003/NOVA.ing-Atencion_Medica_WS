# Referencia de endpoints

### Grafo por cita

#### `GET /semantic/grafo/cita/{id}`

Devuelve el grafo clínico de una cita.

#### `GET /semantic/grafo/cita/{id}/rdf?formato=TURTLE|RDFXML|JSONLD`

Serializa el grafo de una cita.

#### `POST /semantic/grafo/cita/{id}/sparql`

Ejecuta SPARQL sobre el grafo de una cita.

### Grafo del sistema completo

#### `GET /semantic/grafo/sistema/rdf?formato=TURTLE|RDFXML|JSONLD`

Serializa el grafo global del sistema.

#### `POST /semantic/grafo/sistema/sparql`

Ejecuta SPARQL sobre el sistema completo.

### Lenguaje natural

#### `POST /semantic/nl/query`

Recibe una pregunta y devuelve SPARQL generado, resultados tabulares y mensaje de estado.

### Ejemplos rápidos

```bash
curl "http://localhost:8084/semantic/grafo/sistema/rdf?formato=JSONLD"
```

```bash
curl -X POST "http://localhost:8084/semantic/nl/query" \
  -H "Content-Type: application/json" \
  -d "{\"pregunta\":\"lista citas del medico 1\"}"
```
