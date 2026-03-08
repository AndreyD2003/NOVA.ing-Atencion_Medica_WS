# DTOs y contratos

### Contratos explícitos en las fuentes

* `CitaDetalle`
* `CitaEntity`
* respuesta de error `{ "error": "mensaje" }`

### Para qué se usan

#### `CitaDetalle`

Vista enriquecida que reúne:

* `cita`
* `paciente`
* `medico`
* `diagnosticos`

#### Respuesta de error

Contrato simple para errores de negocio en el servicio de citas.
