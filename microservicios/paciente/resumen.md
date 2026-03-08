# Resumen

`msvc-medico` gestiona médicos, horarios y operaciones clínicas asociadas al profesional.

También sirve como punto de entrada para agendar citas y registrar diagnósticos desde la perspectiva médica.

### Responsabilidades

* CRUD de médicos.
* Gestión de horarios.
* Consulta de agenda.
* Agendamiento de citas.
* Registro de diagnósticos vinculados a una cita.

### Entradas principales

* `CrearMedicoDto`
* `MedicoEntity`
* `Cita`
* `Diagnostico`

### Salidas principales

* `MedicoEntity`
* `List<Cita>`
* `Diagnostico` creado en servicio remoto

### Integraciones

#### Consume `msvc-cita`

* listar citas por médico,
* obtener cita por id,
* crear cita.

#### Consume `msvc-diagnostico`

* crear diagnóstico,
* consultar diagnósticos por cita o paciente.

### Reglas de negocio

* Soft delete cambia estado a `INACTIVO`.
* Delete forzado elimina físicamente.
* Al guardar médico se sincroniza `MedicoEntity` ↔ `HorarioMedico`.
* `registrarDiagnostico` valida la existencia de la cita.
* `telefono` usa patrón `^9[0-9]{8}$`.
* `dni` usa patrón `^[1-9][0-9]{7}$`.
* `telefono`, `email` y `dni` deben ser únicos.

### Casos de uso

* Agenda clínica por profesional y especialidad.
* Registro de diagnósticos por médico tratante.
* Planificación asistencial integrada con citas.
