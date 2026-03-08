# Consultar una cita con detalle

Este flujo explica cómo `msvc-cita` arma una vista clínica enriquecida a partir de varias fuentes.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente consulta `GET /citas/con-detalle/{id}`
{% endstep %}

{% step %}
### `CitaServiceImpl.porIdConDetalle` busca la cita local
{% endstep %}

{% step %}
### El servicio enriquece la respuesta

Consulta paciente, médico y diagnósticos mediante clientes remotos.
{% endstep %}

{% step %}
### Devuelve un DTO agregado

La respuesta incluye toda la información disponible para la cita.
{% endstep %}
{% endstepper %}

### Secuencia técnica

```mermaid
sequenceDiagram
  autonumber
  participant U as Usuario/Frontend
  participant CCtrl as CitaController
  participant CSrv as CitaServiceImpl
  participant CRepo as CitaRepository
  participant PCl as PacienteClientRest
  participant MCl as MedicoClientRest
  participant DCl as DiagnosticoClientRest

  U->>CCtrl: GET /citas/con-detalle/{id}
  CCtrl->>CSrv: porIdConDetalle(id)
  CSrv->>CRepo: findById(id)
  CRepo-->>CSrv: Optional<CitaEntity>
  alt cita no existe
    CSrv-->>CCtrl: Optional.empty
    CCtrl-->>U: 404 Not Found
  else existe
    CSrv->>PCl: detalle(pacienteId)
    PCl-->>CSrv: Paciente
    CSrv->>MCl: detalle(medicoId)
    MCl-->>CSrv: Medico
    CSrv->>DCl: listarPorCita(citaId)
    DCl-->>CSrv: Diagnosticos[]
    CSrv-->>CCtrl: CitaDetalle
    CCtrl-->>U: 200 OK
  end
```

### Resultado esperado

La respuesta reúne:

* la cita,
* el paciente,
* el médico,
* los diagnósticos asociados.

### Por qué importa

Esta vista es la base para frontends clínicos y para la construcción del grafo semántico.
