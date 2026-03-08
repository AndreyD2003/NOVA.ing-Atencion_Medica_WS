# Clientes Feign

### Clientes declarados

* `PacienteClientRest`
* `MedicoClientRest`
* `CitaClientRest`
* `DiagnosticoClientRest`

### Endpoints base documentados

* `PacienteClientRest` → `http://localhost:8083/pacientes`
* `MedicoClientRest` → `http://localhost:8080/medicos`
* `CitaClientRest` → `http://localhost:8081/citas`
* `DiagnosticoClientRest` → `http://localhost:8082/diagnosticos`

### Para qué se usan

* recolectar listados de pacientes, médicos, citas y diagnósticos,
* obtener detalle de cita enriquecida,
* construir grafos por cita o del sistema completo.
