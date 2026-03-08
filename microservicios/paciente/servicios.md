# Servicios

### Componentes

* `PacienteService`
* `PacienteServiceImpl`

### Responsabilidades del servicio

* gestionar el CRUD de pacientes,
* consultar citas del paciente,
* consultar historial médico,
* cambiar el estado de una cita.

### Validaciones documentadas

* soft delete cambiando estado a `INACTIVO`,
* eliminación forzada por ID,
* validación de unicidad de `dni`, `telefono` y `email`,
* validación de patrones para `dni` y `telefono`,
* validación de existencia de cita y paciente,
* validación de pertenencia entre `pacienteId` y cita.

### Entradas y salidas más visibles

#### Entradas

* `CrearPacienteDto`
* `PacienteEntity`
* `Map<String, String>` con `estado`

#### Salidas

* `PacienteEntity`
* `List<Cita>`
* `List<Diagnostico>`
* respuestas de error con `Map<String, String>`
