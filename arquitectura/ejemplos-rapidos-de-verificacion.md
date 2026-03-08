# Ejemplos rápidos de verificación

### Cita con detalle

```bash
curl http://localhost:8081/citas/con-detalle/1
```

### Grafo global en JSON-LD

```bash
curl "http://localhost:8084/semantic/grafo/sistema/rdf?formato=JSONLD"
```

### Pregunta en lenguaje natural

```bash
curl -X POST http://localhost:8084/semantic/nl/query \
  -H "Content-Type: application/json" \
  -d "{\"pregunta\":\"lista citas del medico 1\"}"
```
