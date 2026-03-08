# Registrar un diagnóstico desde el módulo médico

Este flujo describe la operación clínica expuesta por `msvc-medico` para registrar diagnósticos vinculados a una cita.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente envía `POST /medicos/registrar-diagnostico`
{% endstep %}

{% step %}
### `MedicoController` delega al servicio
{% endstep %}

{% step %}
### El servicio valida la existencia de la cita

La validación se hace contra `msvc-cita`.
{% endstep %}

{% step %}
### El servicio registra el diagnóstico

La creación se delega a `msvc-diagnostico`.
{% endstep %}

{% step %}
### Se devuelve el diagnóstico creado
{% endstep %}
{% endstepper %}

### Componentes involucrados

* `MedicoController`
* `MedicoService` / `MedicoServiceImpl`
* `CitaClientRest`
* `DiagnosticoClientRest`

### Ejemplo

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
