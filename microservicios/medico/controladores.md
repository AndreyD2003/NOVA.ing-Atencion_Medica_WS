# Controladores

### `MedicoController`

Es la capa REST de `msvc-medico`.

Expone operaciones CRUD, agenda y registro de diagnósticos.

### Operaciones expuestas

#### Consulta

* `GET /medicos`
* `GET /medicos/{id}`
* `GET /medicos/{id}/citas`

#### Escritura

* `POST /medicos`
* `PUT /medicos/{id}`
* `DELETE /medicos/{id}`
* `DELETE /medicos/{id}/force`

#### Operaciones clínicas del médico

* `POST /medicos/agendar-cita`
* `POST /medicos/registrar-diagnostico`
