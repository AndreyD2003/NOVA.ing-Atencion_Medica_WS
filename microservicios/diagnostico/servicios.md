# Servicios

### Componentes

* `DiagnosticoService`
* `DiagnosticoServiceImpl`

### Responsabilidades del servicio

* gestionar diagnósticos,
* consultar por cita y paciente,
* enriquecer detalle con información remota,
* devolver detalle parcial si la integración falla.

### Reglas de negocio documentadas

* `descripcion`, `tipoDiagnostico`, `fechaDiagnostico`, `citaId` y `pacienteId` son obligatorios,
* soft delete con `activo=false`,
* eliminación forzada física,
* enriquecimiento de detalle con cita y paciente,
* tolerancia a fallos de integración mediante respuesta parcial.

### Salidas clave

* `DiagnosticoEntity`
* `DiagnosticoDetalle`
* listas de diagnósticos
