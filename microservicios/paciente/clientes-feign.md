# Clientes Feign

### Clientes declarados

* `CitaClientRest`
* `DiagnosticoClientRest`

### `CitaClientRest`

Se usa para:

* listar citas por paciente,
* crear cita,
* obtener cita por id,
* actualizar cita.

### `DiagnosticoClientRest`

Se usa para:

* listar diagnósticos por paciente.

### Por qué existen

Permiten construir vistas clínicas sin duplicar datos en `msvc-paciente`.
