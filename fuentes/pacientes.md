# Pacientes

`msvc-paciente` gestiona pacientes y expone operaciones clínicas relacionadas.

Además consulta citas y diagnósticos para construir vistas útiles para seguimiento del paciente.

### Responsabilidades

* CRUD de paciente.
* Consulta de citas del paciente.
* Consulta de historial médico.
* Cambio de estado de una cita del paciente propietario.

### Entradas principales

* `CrearPacienteDto`
* `PacienteEntity`
* `Map<String, String>` con `estado`
* `id`, `pacienteId` y `citaId`

### Salidas principales

* `PacienteEntity`
* `List<Cita>`
* `List<Diagnostico>`
* errores con `Map<String, String>`

### Endpoints

* `GET /pacientes`
* `GET /pacientes/{id}`
* `GET /pacientes/{id}/citas`
* `GET /pacientes/{id}/historial-medico`
* `POST /pacientes`
* `PUT /pacientes/{id}`
* `DELETE /pacientes/{id}`
* `DELETE /pacientes/{id}/force`
* `POST /pacientes/agendar-cita`
* `PATCH /pacientes/{pacienteId}/citas/{citaId}/estado`

### Integraciones

#### Consume `msvc-cita`

* listar citas por paciente,
* crear cita,
* obtener cita por id,
* actualizar cita.

#### Consume `msvc-diagnostico`

* listar diagnósticos por paciente.

### Reglas de negocio

* Soft delete cambia estado a `INACTIVO`.
* Delete forzado elimina físicamente por ID.
* `dni`, `telefono` y `email` deben ser únicos.
* `dni` usa patrón `^[1-9][0-9]{7}$`.
* `telefono` usa patrón `^9[0-9]{8}$`.
* `cambiarEstadoCita` valida existencia de cita, paciente y pertenencia.

### Ejemplo

```json
POST /pacientes
{
  "paciente": {
    "nombres": "Lucia",
    "apellidos": "Ramos",
    "fechaNacimiento": "1999-01-01T00:00:00.000+00:00",
    "genero": "FEMENINO",
    "dni": "12345678",
    "telefono": "912345678",
    "email": "lucia.ramos@correo.com",
    "direccion": "Av. Central 123",
    "estado": "ACTIVO",
    "usuarioId": 10
  }
}
```

### Casos de uso

* Portal del paciente con próximas citas.
* Seguimiento del historial clínico.
* Cancelación o autogestión de citas.
