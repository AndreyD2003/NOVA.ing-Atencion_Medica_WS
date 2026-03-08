# 01 Guia Web Semantica Para Principiantes

## 1) Objetivo de esta guía

Aprender a usar la capa semántica del proyecto sin necesidad de conocimientos previos profundos en RDF.

Al finalizar podrás:

* obtener grafos RDF del sistema,
* ejecutar consultas SPARQL,
* probar preguntas en lenguaje natural.

## 2) Conceptos mínimos

* **RDF**: representa datos como tripletas.
* **OWL**: define semántica del dominio y permite inferencias.
* **SPARQL**: permite consultar grafos RDF.
* **JSON-LD**: formato JSON para datos enlazados.

## 3) Endpoints semánticos disponibles

* `GET /semantic/grafo/cita/{id}`
* `GET /semantic/grafo/cita/{id}/rdf?formato=TURTLE|RDFXML|JSONLD`
* `GET /semantic/grafo/sistema/rdf?formato=TURTLE|RDFXML|JSONLD`
* `POST /semantic/grafo/cita/{id}/sparql`
* `POST /semantic/grafo/sistema/sparql`
* `POST /semantic/nl/query`

## 4) Flujo sugerido paso a paso

{% stepper %}
{% step %}
#### Levantar los microservicios

Levanta primero los servicios operacionales (`paciente`, `medico`, `cita`, `diagnostico`) y luego `msvc-web-semantica`.
{% endstep %}

{% step %}
#### Confirmar datos operacionales

Comprueba que existan registros en:

* `GET /pacientes`
* `GET /medicos`
* `GET /citas`
* `GET /diagnosticos`
{% endstep %}

{% step %}
#### Generar grafo RDF

Solicita el grafo completo:

```http
GET /semantic/grafo/sistema/rdf?formato=TURTLE
```
{% endstep %}

{% step %}
#### Ejecutar una consulta SPARQL

Ejemplo de consulta:

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
{% endstep %}

{% step %}
#### Usar lenguaje natural

Ejemplo de petición:

```json
POST /semantic/nl/query
{
  "pregunta": "lista medicos activos"
}
```

La respuesta incluye:

* SPARQL generado,
* resultados tabulares,
* mensaje de estado.
{% endstep %}
{% endstepper %}

## 5) Ejemplos de preguntas soportadas

* `lista medicos`
* `lista medicos activos`
* `lista pacientes activos`
* `lista citas del medico 1`
* `lista citas del paciente 3`
* `lista diagnosticos del paciente 2`
* `lista medico por especialidad cardiologia`

## 6) Buenas prácticas

* Mantener consistencia en enums de estados y tipos para consultas estables.
* Verificar disponibilidad de servicios remotos antes de pruebas semánticas.
* Empezar por consultas SPARQL simples y luego agregar filtros.
* Versionar cambios de ontología y de consultas predefinidas.

## 7) Límites actuales

* El procesamiento de lenguaje natural es rule-based.
* No hay triplestore persistente: el grafo se construye bajo demanda.
* Errores de conectividad entre MSVC impactan los resultados semánticos.
