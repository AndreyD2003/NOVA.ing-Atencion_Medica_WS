# Ejecutar SPARQL manual sobre el sistema

Este flujo cubre la consulta semántica directa sin pasar por lenguaje natural.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente prepara la consulta SPARQL
{% endstep %}

{% step %}
### Envía la consulta a `POST /semantic/grafo/sistema/sparql`
{% endstep %}

{% step %}
### `SparqlQueryServiceImpl` construye o recupera el modelo global
{% endstep %}

{% step %}
### Jena ejecuta `SELECT` sobre el modelo RDF
{% endstep %}

{% step %}
### La API devuelve resultados tabulares
{% endstep %}
{% endstepper %}

### Ejemplo documentado

```sparql
PREFIX onto: <http://nova.ing/ontology/>
SELECT ?cita ?paciente ?medico
WHERE {
  ?cita a onto:Cita ;
        onto:paciente ?paciente ;
        onto:medico ?medico .
}
```

```bash
curl -X POST "http://localhost:8084/semantic/grafo/sistema/sparql" ^
  -H "Content-Type: text/plain" ^
  --data-binary @consulta.sparql
```

### Uso real documentado

Tablero analítico de relaciones clínicas.
