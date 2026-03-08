# Controladores

### `CitaController`

Es la capa REST de `msvc-cita`.

Expone operaciones CRUD y el endpoint de detalle enriquecido.

### Operaciones expuestas

#### Consulta

* `GET /citas`
* `GET /citas/{id}`
* `GET /citas/con-detalle/{id}`
* `GET /citas/paciente/{id}`
* `GET /citas/medico/{id}`

#### Escritura

* `POST /citas`
* `PUT /citas/{id}`
* `DELETE /citas/{id}`
* `DELETE /citas/{id}/force`

### Qué delega

* CRUD de citas,
* consulta por paciente y médico,
* construcción de `CitaDetalle`,
* validaciones de agenda y disponibilidad.
