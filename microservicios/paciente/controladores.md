# Controladores

### `PacienteController`

Es la capa REST de `msvc-paciente`.

Recibe peticiones HTTP y delega la lógica al servicio del dominio.

### Operaciones expuestas

#### Consulta

* `GET /pacientes`
* `GET /pacientes/{id}`
* `GET /pacientes/{id}/citas`
* `GET /pacientes/{id}/historial-medico`

#### Escritura

* `POST /pacientes`
* `PUT /pacientes/{id}`
* `DELETE /pacientes/{id}`
* `DELETE /pacientes/{id}/force`

#### Operaciones clínicas del paciente

* `POST /pacientes/agendar-cita`
* `PATCH /pacientes/{pacienteId}/citas/{citaId}/estado`

### Qué delega

* CRUD del paciente.
* Consulta de citas remotas.
* Consulta de historial médico remoto.
* Cambio de estado de una cita del paciente propietario.
