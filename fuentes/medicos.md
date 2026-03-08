# Médicos

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

### Endpoints

* `GET /medicos`
* `GET /medicos/{id}`
* `GET /medicos/{id}/citas`
* `POST /medicos`
* `PUT /medicos/{id}`
* `DELETE /medicos/{id}`
* `DELETE /medicos/{id}/force`
* `POST /medicos/agendar-cita`
* `POST /medicos/registrar-diagnostico`

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

### Ejemplo

```json
POST /medicos
{
  "medico": {
    "nombres": "Andrea",
    "apellidos": "Quispe",
    "especialidad": "CARDIOLOGIA",
    "telefono": "934567890",
    "email": "andrea.quispe@correo.com",
    "dni": "87654321",
    "estado": "ACTIVO",
    "usuarioId": 20,
    "horarios": [
      { "diaSemana": "MONDAY", "horaInicio": "08:00:00", "horaFin": "12:00:00" }
    ]
  }
}
```

### Casos de uso

* Agenda clínica por profesional y especialidad.
* Registro de diagnósticos por médico tratante.
* Planificación asistencial integrada con citas.
