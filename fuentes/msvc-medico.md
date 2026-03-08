# msvc medico

## Propósito

Gestionar médicos, sus horarios y operaciones clínicas relacionadas:

* CRUD de médicos.
* Consulta de agenda de citas.
* Agendamiento de citas desde módulo médico.
* Registro de diagnósticos vinculados a cita.

## Entradas y salidas principales

### Entradas

* `CrearMedicoDto` para creación.
* `MedicoEntity` para edición.
* `Cita` para agendar cita.
* `Diagnostico` para registrar diagnóstico.

### Salidas

* `MedicoEntity` y listas de médicos.
* `List<Cita>` para agenda.
* `Diagnostico` creado en servicio remoto.

## Endpoints

* `GET /medicos`
* `GET /medicos/{id}`
* `GET /medicos/{id}/citas`
* `POST /medicos`
* `PUT /medicos/{id}`
* `DELETE /medicos/{id}`
* `DELETE /medicos/{id}/force`
* `POST /medicos/agendar-cita`
* `POST /medicos/registrar-diagnostico`

## Dependencias internas

* `MedicoController`
* `MedicoService` / `MedicoServiceImpl`
* `MedicoRepository`
* `MedicoEntity`
* `HorarioMedico`
* Enums: `EspecialidadMedico`, `EstadoMedico`

## Dependencias externas

* `CitaClientRest` (`msvc-cita`):
  * listar citas por médico,
  * obtener cita por id,
  * crear cita.
* `DiagnosticoClientRest` (`msvc-diagnostico`):
  * crear diagnóstico,
  * consultar diagnósticos por cita/paciente.

## Reglas y consideraciones importantes

{% hint style="warning" %}
* Soft delete de médico: cambia estado a `INACTIVO`.
* Delete forzado: eliminación física.
* Al guardar médico, se sincroniza relación bidireccional `MedicoEntity` ↔ `HorarioMedico`.
* `registrarDiagnostico` valida existencia de cita en `msvc-cita`.
* Validaciones de entidad:
  * `telefono` patrón `^9[0-9]{8}$`,
  * `dni` patrón `^[1-9][0-9]{7}$`,
  * unicidad de `telefono`, `email`, `dni`.
{% endhint %}

## Ejemplos de implementación

### Crear médico con horarios

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

### Registrar diagnóstico

```json
POST /medicos/registrar-diagnostico
{
  "descripcion": "Paciente con mejoría clínica",
  "tipoDiagnostico": "DEFINITIVO",
  "fechaDiagnostico": "2026-03-01T10:30:00.000+00:00",
  "citaId": 15,
  "pacienteId": 3
}
```

## Casos de uso

* Agenda clínica por especialidad y profesional.
* Registro de diagnóstico por médico tratante.
* Integración con módulo de citas para planificación asistencial.
