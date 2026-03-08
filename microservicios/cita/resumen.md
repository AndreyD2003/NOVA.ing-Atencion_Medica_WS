# Resumen

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

### Casos de uso

* Agenda central del sistema.
* Validación de disponibilidad.
* Fuente principal para frontends clínicos.
* Fuente principal para la capa semántica.
