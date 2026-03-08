# Controladores

### `DiagnosticoController`

Es la capa REST de `msvc-diagnostico`.

Expone operaciones CRUD, consultas por contexto y detalle enriquecido.

### Operaciones expuestas

#### Consulta

* `GET /diagnosticos`
* `GET /diagnosticos/{id}`
* `GET /diagnosticos/con-detalle/{id}`
* `GET /diagnosticos/cita/{id}`
* `GET /diagnosticos/paciente/{id}`

#### Escritura

* `POST /diagnosticos`
* `PUT /diagnosticos/{id}`
* `DELETE /diagnosticos/{id}`
* `DELETE /diagnosticos/{id}/force`
