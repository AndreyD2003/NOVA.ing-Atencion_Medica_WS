# DTOs y contratos

### DTOs y contratos explícitos en las fuentes

* `CrearPacienteDto`
* `Map<String, String>` para cambio de estado
* listas de `Cita`
* listas de `Diagnostico`

### Para qué se usan

#### `CrearPacienteDto`

Contrato de entrada para crear pacientes.

#### `Map<String, String>` con `estado`

Contrato simple para `PATCH /pacientes/{pacienteId}/citas/{citaId}/estado`.

#### `List<Cita>`

Salida usada para mostrar citas del paciente.

#### `List<Diagnostico>`

Salida usada para construir el historial médico.
