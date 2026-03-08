# Crear una cita

Este flujo describe cómo el sistema crea una cita y valida disponibilidad antes de persistirla.

### Secuencia funcional

{% stepper %}
{% step %}
### El cliente envía `POST /citas`

La petición entra por `CitaController`.
{% endstep %}

{% step %}
### `CitaController` delega a `CitaServiceImpl.guardar`

La lógica de negocio ocurre en el servicio.
{% endstep %}

{% step %}
### El servicio normaliza la fecha

`fechaCita` se normaliza para evitar inconsistencias por tiempo.
{% endstep %}

{% step %}
### Valida conflictos de horario

Se verifica conflicto por médico y por paciente.
{% endstep %}

{% step %}
### Consulta médico y paciente

Se valida existencia y estado `ACTIVO` en ambos servicios remotos.
{% endstep %}

{% step %}
### Guarda la cita

Si no hay conflicto y las entidades son válidas, la cita se persiste.
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
  participant MCl as MedicoClientRest
  participant PCl as PacienteClientRest
  participant DB as BD Citas

  U->>CCtrl: POST /citas
  CCtrl->>CSrv: guardar(cita)
  CSrv->>CSrv: normalizar fechaCita
  CSrv->>CRepo: existsConflictMedico(...)
  CRepo-->>CSrv: resultado
  CSrv->>CRepo: existsConflictPaciente(...)
  CRepo-->>CSrv: resultado
  alt hay conflicto
    CSrv-->>CCtrl: RuntimeException
    CCtrl-->>U: 400 Bad Request
  else sin conflicto
    CSrv->>MCl: detalle(medicoId)
    MCl-->>CSrv: Medico
    CSrv->>PCl: detalle(pacienteId)
    PCl-->>CSrv: Paciente
    alt médico/paciente inválido
      CSrv-->>CCtrl: RuntimeException
      CCtrl-->>U: 400 Bad Request
    else válido
      CSrv->>CRepo: save(cita)
      CRepo->>DB: persistir
      DB-->>CRepo: OK
      CRepo-->>CSrv: entidad guardada
      CSrv-->>CCtrl: entidad guardada
      CCtrl-->>U: 201 Created
    end
  end
```

### Qué protege este flujo

* evita solapes,
* evita citas con entidades inactivas,
* asegura una agenda consistente.
