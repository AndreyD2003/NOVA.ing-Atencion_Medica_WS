# Referencia de endpoints

### Consulta de pacientes

#### `GET /pacientes`

Lista pacientes.

#### `GET /pacientes/{id}`

Obtiene un paciente por ID.

### Consulta clínica del paciente

#### `GET /pacientes/{id}/citas`

Devuelve `List<Cita>`.

#### `GET /pacientes/{id}/historial-medico`

Devuelve `List<Diagnostico>`.

### Escritura de pacientes

#### `POST /pacientes`

Crea un paciente usando `CrearPacienteDto`.

#### `PUT /pacientes/{id}`

Actualiza un paciente usando `PacienteEntity`.

### Borrado

#### `DELETE /pacientes/{id}`

Aplica soft delete cambiando el estado a `INACTIVO`.

#### `DELETE /pacientes/{id}/force`

Elimina físicamente el registro.

### Operaciones del paciente sobre citas

#### `POST /pacientes/agendar-cita`

Agenda una cita desde el módulo de pacientes.

#### `PATCH /pacientes/{pacienteId}/citas/{citaId}/estado`

Cambia el estado de una cita si la cita pertenece al paciente.

#### Ejemplo

```json
PATCH /pacientes/1/citas/25/estado
{
  "estado": "CANCELADA"
}
```
