# Consultas y ejemplos

### Endpoints disponibles

* `GET /semantic/grafo/cita/{id}`
* `GET /semantic/grafo/cita/{id}/rdf?formato=TURTLE|RDFXML|JSONLD`
* `POST /semantic/grafo/cita/{id}/sparql`
* `GET /semantic/grafo/sistema/rdf?formato=TURTLE|RDFXML|JSONLD`
* `POST /semantic/grafo/sistema/sparql`
* `POST /semantic/nl/query`

### Flujo recomendado

{% stepper %}
{% step %}
### Levantar servicios operacionales

Primero `paciente`, `medico`, `cita` y `diagnostico`.
{% endstep %}

{% step %}
### Levantar `msvc-web-semantica`
{% endstep %}

{% step %}
### Confirmar datos operacionales

* `GET /pacientes`
* `GET /medicos`
* `GET /citas`
* `GET /diagnosticos`
{% endstep %}

{% step %}
### Generar RDF del sistema

```http
GET /semantic/grafo/sistema/rdf?formato=TURTLE
```
{% endstep %}

{% step %}
### Ejecutar SPARQL o lenguaje natural

Usa SPARQL manual o `POST /semantic/nl/query`.
{% endstep %}
{% endstepper %}

### Ejemplo SPARQL

```sparql
PREFIX onto: <http://nova.ing/ontology/>
SELECT ?paciente ?nombre
WHERE {
  ?paciente a onto:Paciente ;
            onto:nombres ?nombre .
}
```

Enviar como texto plano a:

```http
POST /semantic/grafo/sistema/sparql
```

### Ejemplo en lenguaje natural

```json
POST /semantic/nl/query
{
  "pregunta": "lista medicos activos"
}
```

### Preguntas soportadas

* `lista medicos`
* `lista medicos activos`
* `lista pacientes activos`
* `lista citas del medico 1`
* `lista citas del paciente 3`
* `lista diagnosticos del paciente 2`
* `lista medico por especialidad cardiologia`

### Buenas prácticas

* Mantener consistencia en enums y estados.
* Verificar conectividad de servicios remotos.
* Empezar con consultas simples.
* Versionar cambios en ontología y consultas.
