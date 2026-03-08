# Servicios

### Componentes

* `MedicoService`
* `MedicoServiceImpl`

### Responsabilidades del servicio

* gestionar médicos,
* sincronizar horarios,
* consultar agenda remota,
* agendar citas,
* registrar diagnósticos.

### Validaciones documentadas

* soft delete cambiando estado a `INACTIVO`,
* eliminación forzada,
* sincronización bidireccional `MedicoEntity` ↔ `HorarioMedico`,
* validación de existencia de cita antes de registrar diagnóstico,
* unicidad de `telefono`, `email` y `dni`,
* validaciones de formato para `telefono` y `dni`.

### Entradas y salidas principales

#### Entradas

* `CrearMedicoDto`
* `MedicoEntity`
* `Cita`
* `Diagnostico`

#### Salidas

* `MedicoEntity`
* `List<Cita>`
* `Diagnostico` creado en servicio remoto
