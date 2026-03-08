# Exportar RDF del sistema completo

Este flujo permite obtener una representación semántica completa del sistema.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente llama `GET /semantic/grafo/sistema/rdf`
{% endstep %}

{% step %}
### El servicio semántico consulta pacientes, médicos, citas y diagnósticos
{% endstep %}

{% step %}
### Construye el modelo RDF global
{% endstep %}

{% step %}
### Aplica razonamiento OWL
{% endstep %}

{% step %}
### Serializa en `TURTLE`, `RDFXML` o `JSONLD`
{% endstep %}
{% endstepper %}

### Uso real documentado

Entregar datos clínicos a un consumidor que entiende JSON-LD.

### Ejemplo

```bash
curl "http://localhost:8084/semantic/grafo/sistema/rdf?formato=JSONLD"
```
