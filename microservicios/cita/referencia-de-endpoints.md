# Referencia de endpoints

### Consulta básica

#### `GET /citas`

Lista citas.

#### `GET /citas/{id}`

Obtiene una cita por ID.

### Consulta contextual

#### `GET /citas/paciente/{id}`

Lista citas de un paciente.

#### `GET /citas/medico/{id}`

Lista citas de un médico.

#### `GET /citas/con-detalle/{id}`

Devuelve `CitaDetalle` con cita, paciente, médico y diagnósticos.

### Escritura

#### `POST /citas`

Crea una cita validando solapes y estado activo de médico y paciente.

#### `PUT /citas/{id}`

Actualiza una cita.

### Borrado

#### `DELETE /citas/{id}`

Aplica soft delete cambiando el estado a `CANCELADA`.

#### `DELETE /citas/{id}/force`

Elimina físicamente el registro.

#### Ejemplo

```json
POST /citas
{
  "fechaCita": "2026-03-10T00:00:00.000+00:00",
  "horaInicio": "09:00:00",
  "horaFin": "09:30:00",
  "motivo": "Control general",
  "estado": "PROGRAMADA",
  "pacienteId": 1,
  "medicoId": 2
}
```
