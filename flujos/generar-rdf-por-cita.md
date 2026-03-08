# Generar RDF por cita

Este flujo transforma una cita enriquecida en un modelo RDF serializable.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente llama `GET /semantic/grafo/cita/{id}/rdf`
{% endstep %}

{% step %}
### `SemanticGraphController` delega a `RdfGraphServiceImpl`
{% endstep %}

{% step %}
### `SemanticGraphServiceImpl` obtiene la cita con detalle

La fuente remota es `msvc-cita`.
{% endstep %}

{% step %}
### El dato agregado se mapea a DTO semántico
{% endstep %}

{% step %}
### `RdfModelBuilder` construye el modelo RDF
{% endstep %}

{% step %}
### Se aplica razonamiento OWL
{% endstep %}

{% step %}
### El modelo se serializa

Los formatos soportados son `TURTLE`, `RDFXML` y `JSONLD`.
{% endstep %}
{% endstepper %}

### Secuencia técnica

```mermaid
sequenceDiagram
  autonumber
  participant U as Usuario/Frontend
  participant SGC as SemanticGraphController
  participant RGS as RdfGraphServiceImpl
  participant SGS as SemanticGraphServiceImpl
  participant CCl as CitaClientRest
  participant RMB as RdfModelBuilder
  participant Jena as Jena RDF/OWL

  U->>SGC: GET /semantic/grafo/cita/{id}/rdf?formato=TURTLE
  SGC->>RGS: serializarModeloPorCitaId(id, formato)
  RGS->>RGS: construirModeloPorCitaId(id)
  RGS->>SGS: construirGrafoPorCitaId(id)
  SGS->>CCl: obtenerCitaConDetalle(id)
  CCl-->>SGS: CitaDetalleRemoteDto
  SGS-->>RGS: GrafoClinicoDto
  RGS->>RMB: fromGrafoClinico(grafo)
  RMB-->>RGS: Model
  RGS->>Jena: aplicarRazonamientoOwl(model)
  Jena-->>RGS: InfModel
  RGS->>Jena: serializar(formato)
  Jena-->>RGS: RDF texto
  RGS-->>SGC: RDF texto
  SGC-->>U: 200 + Content-Type semántico
```

### Transformación de datos

```
JSON remoto
→ DTO remoto
→ DTO semántico
→ Modelo RDF
→ Modelo inferido
→ Texto RDF
```
