# Citas

`msvc-cita` es el núcleo operativo del sistema.

Coordina el agendamiento, valida disponibilidad y construye la vista clínica enriquecida de una cita.

### Responsabilidades

* CRUD de citas.
* Consulta por paciente y por médico.
* Vista enriquecida de cita.
* Validación de disponibilidad de médico y paciente.

### Entradas principales

* `CitaEntity`
* identificadores de cita, paciente y médico

### Salidas principales

* `CitaEntity`
* `CitaDetalle`
* errores de negocio con `{ "error": "mensaje" }`

### Endpoints

* `GET /citas`
* `GET /citas/{id}`
* `GET /citas/con-detalle/{id}`
* `GET /citas/paciente/{id}`
* `GET /citas/medico/{id}`
* `POST /citas`
* `PUT /citas/{id}`
* `DELETE /citas/{id}`
* `DELETE /citas/{id}/force`

### Integraciones

#### Consume `msvc-paciente`

* detalle de paciente.

#### Consume `msvc-medico`

* detalle de médico.

#### Consume `msvc-diagnostico`

* diagnósticos por cita.

### Reglas de negocio

* Normaliza `fechaCita` a medianoche.
* Evita solapes para médico y paciente.
* Excluye citas `CANCELADA` y `REALIZADA` en la validación de conflicto.
* Antes de guardar valida que médico y paciente existan.
* Antes de guardar valida que ambos estén en estado `ACTIVO`.
* Soft delete cambia estado a `CANCELADA`.
* Delete forzado elimina físicamente.

### Ejemplo

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

### Respuesta enriquecida

`GET /citas/con-detalle/{id}` devuelve:

* `cita`
* `paciente`
* `medico`
* `diagnosticos`

### Casos de uso

* Agenda central del sistema.
* Validación de disponibilidad.
* Fuente principal para frontends clínicos.
* Fuente principal para la capa semántica.
