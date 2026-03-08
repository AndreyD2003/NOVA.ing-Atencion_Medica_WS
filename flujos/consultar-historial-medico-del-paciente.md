# Consultar historial médico del paciente

Este flujo usa `msvc-paciente` como punto de entrada para revisar el historial clínico del paciente.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente consulta `GET /pacientes/{id}/historial-medico`
{% endstep %}

{% step %}
### `PacienteController` delega al servicio
{% endstep %}

{% step %}
### El servicio consulta `msvc-diagnostico`

Recupera la lista de diagnósticos asociados al paciente.
{% endstep %}

{% step %}
### Se devuelve `List<Diagnostico>`
{% endstep %}
{% endstepper %}

### Componentes involucrados

* `PacienteController`
* `PacienteService` / `PacienteServiceImpl`
* `DiagnosticoClientRest`

### Resultado

Sirve para seguimiento clínico e historial del paciente.
