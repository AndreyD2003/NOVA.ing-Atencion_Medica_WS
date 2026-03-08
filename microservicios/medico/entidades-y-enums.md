# Entidades y enums

### Entidades

* `MedicoEntity`
* `HorarioMedico`

### Enums

* `EspecialidadMedico`
* `EstadoMedico`

### Qué representan

#### `MedicoEntity`

Entidad persistente principal del microservicio.

#### `HorarioMedico`

Representa la disponibilidad del médico por día y rango horario.

#### `EspecialidadMedico`

Enum de especialidades del dominio.

#### `EstadoMedico`

Enum del estado del médico.

Se usa para soft delete mediante cambio a `INACTIVO`.
