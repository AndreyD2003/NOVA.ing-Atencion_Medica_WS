# Resumen

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

### Casos de uso

* Historial clínico del paciente.
* Seguimiento diagnóstico por cita.
* Fuente semántica para eventos clínicos.
