# Clientes Feign

### Clientes declarados

* `CitaClientRest`
* `PacienteClientRest`

### `CitaClientRest`

Se usa para obtener el detalle de la cita.

### `PacienteClientRest`

Se usa para obtener el detalle del paciente.

### Por qué existen

Permiten construir `DiagnosticoDetalle` sin duplicar los datos de otros dominios.
