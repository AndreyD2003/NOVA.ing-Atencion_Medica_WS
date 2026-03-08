# Objetivo y recorrido recomendado

Esta sección permite usar la capa semántica del proyecto sin requerir conocimientos profundos previos en RDF.

### Al finalizar esta sección podrás

* obtener grafos RDF del sistema,
* ejecutar consultas SPARQL,
* probar preguntas en lenguaje natural.

### Recorrido sugerido

1. [Conceptos base](conceptos-base.md)
2. [msvc-web-semantica](../fuentes/msvc-web-semantica-1.md)
3. [Consultas y ejemplos](modulo-semantico/consultas-y-ejemplos.md)
4. [Límites y restricciones actuales](../arquitectura/ejemplos-rapidos-de-verificacion.md)

### Orden recomendado de prueba

{% stepper %}
{% step %}
### Levantar servicios operacionales

`paciente`, `medico`, `cita` y `diagnostico`.
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
### Probar RDF, SPARQL y lenguaje natural

Usar los endpoints de la sección semántica.
{% endstep %}
{% endstepper %}
