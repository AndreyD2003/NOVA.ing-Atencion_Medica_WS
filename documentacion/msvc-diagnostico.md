# MSVC Diagnóstico — Documentación Técnica

## Propósito

Gestionar diagnósticos clínicos asociados a citas y pacientes:

- CRUD de diagnóstico.
- Consultas por cita y por paciente.
- Vista detallada con enriquecimiento remoto.

## Entradas y salidas principales

### Entradas

- `DiagnosticoEntity` para creación y edición.
- Identificadores de diagnóstico, cita y paciente.

### Salidas

- `DiagnosticoEntity` y listas de diagnósticos.
- `DiagnosticoDetalle` con recursos relacionados.

## Endpoints

- `GET /diagnosticos`
- `GET /diagnosticos/{id}`
- `GET /diagnosticos/con-detalle/{id}`
- `GET /diagnosticos/cita/{id}`
- `GET /diagnosticos/paciente/{id}`
- `POST /diagnosticos`
- `PUT /diagnosticos/{id}`
- `DELETE /diagnosticos/{id}`
- `DELETE /diagnosticos/{id}/force`

## Dependencias internas

- `DiagnosticoController`
- `DiagnosticoService` / `DiagnosticoServiceImpl`
- `DiagnosticoRepository`
- `DiagnosticoEntity`
- Enum `TipoDiagnostico`

## Dependencias externas

- `CitaClientRest` (`msvc-cita`) para detalle de cita.
- `PacienteClientRest` (`msvc-paciente`) para detalle de paciente.

## Reglas y consideraciones importantes

- `descripcion`, `tipoDiagnostico`, `fechaDiagnostico`, `citaId`, `pacienteId` son obligatorios.
- Soft delete de diagnóstico usando `activo=false`.
- Delete forzado elimina físicamente el registro.
- `porIdConDetalle` intenta enriquecer con cita y paciente; si falla integración, retorna detalle parcial.

## Ejemplos de implementación

### Crear diagnóstico

```json
POST /diagnosticos
{
  "descripcion": "Infección respiratoria leve",
  "tipoDiagnostico": "PRESUNTIVO",
  "fechaDiagnostico": "2026-03-05T11:00:00.000+00:00",
  "citaId": 8,
  "pacienteId": 1
}
```

### Consultar diagnósticos por cita

```http
GET /diagnosticos/cita/8
```

## Casos de uso

- Historial clínico del paciente.
- Evolución diagnóstica de una cita.
- Fuente principal para modelado semántico de eventos clínicos.
