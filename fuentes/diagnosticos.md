# Diagnósticos

`msvc-diagnostico` administra diagnósticos clínicos asociados a citas y pacientes.

También puede devolver una vista enriquecida cuando necesita cruzar información con otros servicios.

### Responsabilidades

* CRUD de diagnóstico.
* Consulta por cita.
* Consulta por paciente.
* Vista detallada con enriquecimiento remoto.

### Entradas principales

* `DiagnosticoEntity`
* identificadores de diagnóstico, cita y paciente

### Salidas principales

* `DiagnosticoEntity`
* listas de diagnósticos
* `DiagnosticoDetalle`

### Endpoints

* `GET /diagnosticos`
* `GET /diagnosticos/{id}`
* `GET /diagnosticos/con-detalle/{id}`
* `GET /diagnosticos/cita/{id}`
* `GET /diagnosticos/paciente/{id}`
* `POST /diagnosticos`
* `PUT /diagnosticos/{id}`
* `DELETE /diagnosticos/{id}`
* `DELETE /diagnosticos/{id}/force`

### Integraciones

#### Consume `msvc-cita`

* detalle de cita.

#### Consume `msvc-paciente`

* detalle de paciente.

### Reglas de negocio

* `descripcion`, `tipoDiagnostico`, `fechaDiagnostico`, `citaId` y `pacienteId` son obligatorios.
* Soft delete usa `activo=false`.
* Delete forzado elimina físicamente el registro.
* `porIdConDetalle` intenta enriquecer con cita y paciente.
* Si falla la integración, devuelve detalle parcial.

### Ejemplo

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

### Casos de uso

* Historial clínico del paciente.
* Seguimiento diagnóstico por cita.
* Fuente semántica para eventos clínicos.
