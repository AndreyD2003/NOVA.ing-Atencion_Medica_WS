# Conceptos base

La capa semántica hace que los datos no solo sean legibles, sino también interpretables por máquina.

En este proyecto, eso permite consultar relaciones clínicas sin depender de la estructura interna de cada microservicio.

### Idea central

En una API tradicional, JSON expone datos.

En la capa semántica, esos datos además llevan significado explícito: qué es cada entidad y cómo se relaciona con otras.

### Analogías útiles

#### Biblioteca

* Sin capa semántica: libros con etiquetas sueltas.
* Con capa semántica: catálogo con autor, tema y relaciones.

#### Mapa de metro

* cada entidad es una estación,
* cada relación es una línea,
* el grafo permite recorrer información conectada.

### Conceptos mínimos

* **RDF**: modelo de tripletas.
* **OWL**: reglas semánticas e inferencias.
* **SPARQL**: lenguaje de consulta para grafos.
* **IRI**: identificador global de recursos.
* **JSON-LD**: formato JSON para datos enlazados.

### Qué representa este proyecto

Las entidades principales del grafo son:

* `Paciente`
* `Medico`
* `Cita`
* `Diagnostico`
* `Horario`

También usa clases auxiliares de estado y tipo.
