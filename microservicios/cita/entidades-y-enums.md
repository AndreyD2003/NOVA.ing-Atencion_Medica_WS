# Entidades y enums

### Entidades

* `CitaEntity`

### Enums

* `EstadoCita`

### Qué representan

#### `CitaEntity`

Entidad persistente principal del microservicio.

#### `EstadoCita`

Enum del ciclo de vida de la cita.

También participa en reglas como:

* soft delete hacia `CANCELADA`,
* exclusión de `CANCELADA` y `REALIZADA` en validación de conflictos.
