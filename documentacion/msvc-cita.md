# MSVC Cita — Documentación Técnica

## Propósito

Gestionar el ciclo de citas médicas y actuar como núcleo de coordinación clínica:

- CRUD de citas.
- Consulta por paciente y médico.
- Vista enriquecida de cita con datos cruzados.
- Validación de disponibilidad de médico y paciente.

## Entradas y salidas principales

### Entradas

- `CitaEntity` en creación/edición.
- Identificadores de cita/paciente/médico.

### Salidas

- `CitaEntity` para operaciones directas.
- `CitaDetalle` para endpoint enriquecido.
- Errores de negocio en formato `{ "error": "mensaje" }`.

## Endpoints

- `GET /citas`
- `GET /citas/{id}`
- `GET /citas/con-detalle/{id}`
- `GET /citas/paciente/{id}`
- `GET /citas/medico/{id}`
- `POST /citas`
- `PUT /citas/{id}`
- `DELETE /citas/{id}`
- `DELETE /citas/{id}/force`

## Dependencias internas

- `CitaController`
- `CitaService` / `CitaServiceImpl`
- `CitaRepository`
- `CitaEntity`
- Enum `EstadoCita`

## Dependencias externas

- `PacienteClientRest` (`msvc-paciente`):
  - detalle de paciente.
- `MedicoClientRest` (`msvc-medico`):
  - detalle de médico.
- `DiagnosticoClientRest` (`msvc-diagnostico`):
  - diagnósticos por cita.

## Reglas y consideraciones importantes

- Normaliza `fechaCita` a medianoche para evitar inconsistencias por componente de tiempo.
- Evita solapes:
  - conflicto por médico,
  - conflicto por paciente,
  - excluyendo citas `CANCELADA` y `REALIZADA`.
- Antes de guardar valida que médico y paciente existan y estén en estado `ACTIVO`.
- Soft delete de cita: estado cambia a `CANCELADA`.
- Delete forzado: eliminación física.

## Ejemplos de implementación

### Crear cita

```json
POST /citas
{
  "fechaCita": "2026-03-10T00:00:00.000+00:00",
  "horaInicio": "09:00:00",
  "horaFin": "09:30:00",
  "motivo": "Control general",
  "estado": "PROGRAMADA",
  "pacienteId": 1,
  "medicoId": 2
}
```

### Obtener cita enriquecida

```http
GET /citas/con-detalle/12
```

Respuesta esperada:

- objeto `cita`,
- objeto `paciente`,
- objeto `medico`,
- lista `diagnosticos`.

## Casos de uso

- Agenda central para múltiples módulos.
- Validación de disponibilidad antes de confirmar cita.
- Consulta consolidada para frontends clínicos y para capa semántica.
