# Servicios

### Componentes

* `CitaService`
* `CitaServiceImpl`

### Responsabilidades del servicio

* gestionar el ciclo de citas,
* normalizar `fechaCita`,
* validar conflictos de horario,
* validar existencia y estado de médico y paciente,
* construir la vista enriquecida `CitaDetalle`.

### Reglas de negocio documentadas

* normalización de `fechaCita` a medianoche,
* validación de conflictos por médico,
* validación de conflictos por paciente,
* exclusión de `CANCELADA` y `REALIZADA` en la validación de solape,
* validación remota de médico y paciente en estado `ACTIVO`,
* soft delete cambiando estado a `CANCELADA`,
* eliminación forzada del registro.

### Salidas clave

* `CitaEntity`
* `CitaDetalle`
* errores con `{ "error": "mensaje" }`
