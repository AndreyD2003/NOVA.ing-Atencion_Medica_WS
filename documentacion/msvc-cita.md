# MSVC Cita — Documentación

> Nota de versión actual: este servicio ya no aplica filtros JWT ni reglas de seguridad en código; cualquier sección que mencione JWT, JwtTokenValidator o @PreAuthorize se considera documentación histórica.

## Propósito
- Gestiona el ciclo de vida de citas médicas: creación, consulta, edición, cancelación y cambio de estado.
- Orquesta datos de paciente y médico, y enlaza diagnósticos asociados.

## Estructura Interna
- Controller: [CitaController](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/controllers/CitaController.java)
- Service: [CitaService](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/services/CitaService.java), [CitaServiceImpl](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/services/implementation/CitaServiceImpl.java)
- Repository: [CitaRepository](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/repositories/CitaRepository.java)
- Entidad: [CitaEntity](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/models/entities/CitaEntity.java)
- Enums: Estado de cita ([EstadoCita](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/enums/EstadoCita.java))
- Seguridad: filtro JWT y utilidades; reglas por @PreAuthorize.
- Feign Clients: [PacienteClientRest](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/clients/PacienteClientRest.java), [MedicoClientRest](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/clients/MedicoClientRest.java), [DiagnosticoClientRest](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/clients/DiagnosticoClientRest.java)
- Feign Interceptor: [FeignInterceptorConfig](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-cita/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/cita/config/FeignInterceptorConfig.java)

## Ciclo de Funcionamiento por Clase
- CitaController:
  - Recibe solicitudes REST; valida rol/propiedad; delega en servicio.
  - Expone listados por paciente/médico y cambio de estado con reglas.
- CitaServiceImpl:
  - Normaliza fecha; valida solapes por repositorio; consulta MSVCs remotos para verificar estado de entidades; guarda/actualiza.
  - Construye detalle con paciente, médico y diagnósticos vía Feign.
- CitaRepository:
  - findByPacienteId / findByMedicoId
  - existsConflictMedico / existsConflictPaciente para evitar solapes activos.
- CitaEntity:
  - Modelo de persistencia con campos obligatorios y estado de negocio.
- JwtTokenValidator/JwtUtils:
  - Valida token y extrae userId; habilita contexto de seguridad para reglas.
- Feign Clients + Interceptor:
  - Propagan Authorization para validar acceso en servicios remotos.

## Flujo de Funcionamiento
```mermaid
sequenceDiagram
  participant C as Cliente
  participant CIT as CitaController
  participant S as CitaServiceImpl
  participant R as CitaRepository
  participant PAC as PacienteClient
  participant MED as MedicoClient
  participant DIA as DiagnosticoClient
  C->>CIT: POST /citas
  CIT->>S: guardar(cita)
  S->>R: existsConflictMedico/Paciente
  R-->>S: false (no solape)
  S->>MED: detalle(medicoId)
  S->>PAC: detalle(pacienteId)
  S->>R: save(cita)
  R-->>S: cita
  S-->>CIT: cita
  CIT-->>C: 201
```

## Catálogo de Endpoints
- GET /citas (ADMIN)
- GET /citas/{id}
- GET /citas/con-detalle/{id}
- GET /citas/paciente/{id} (propiedad paciente aplicada)
- GET /citas/medico/{id} (propiedad médico aplicada)
- POST /citas (DOCTOR, ADMIN, PATIENT, RECEPTIONIST con reglas)
- PUT /citas/{id} (DOCTOR, ADMIN)
- DELETE /citas/{id}
- DELETE /citas/{id}/force (ADMIN)
- PATCH /citas/{id}/estado (ADMIN, DOCTOR, PATIENT con reglas)

## Reglas de Validación
- CitaEntity: fechaCita, horaInicio, horaFin, motivo, estado, pacienteId, medicoId obligatorios.
- Solapes: se evita cuando estado no es CANCELADA/REALIZADA y hay intersección de tiempo.
- Estados:
  - Paciente: solo CANCELADA sobre sus citas.
  - Doctor: REALIZADA solo si estaba PROGRAMADA y es su cita.
  - Recepcionista: no puede marcar REALIZADA.

## Diagrama ER
```mermaid
erDiagram
  CITA {
    Long id
    Date fechaCita
    Time horaInicio
    Time horaFin
    EstadoCita estado
    Long pacienteId
    Long medicoId
  }
```

## Diagramas Adicionales
- Diagrama de Estados de Cita
```mermaid
stateDiagram-v2
  [*] --> PROGRAMADA
  PROGRAMADA --> CANCELADA: Paciente/Recepcionista/Administrador
  PROGRAMADA --> REALIZADA: Doctor/Administrador
  CANCELADA --> [*]
  REALIZADA --> [*]
```

- Secuencia: Listar citas por Paciente con validación de propiedad
```mermaid
sequenceDiagram
  participant P as Paciente
  participant CIT as CitaController
  participant S as CitaService
  P->>CIT: GET /citas/paciente/{id}
  CIT->>CIT: Validar rol PATIENT y propiedad
  CIT->>S: listarPorPaciente(id)
  S-->>CIT: lista
  CIT-->>P: 200 OK o 403
```

- Actividad: Crear cita (reglas y validaciones)
```mermaid
flowchart TD
  A[Ingresar datos de cita] --> B{Paciente/Doctor/Recepcionista?}
  B -->|Paciente| C[Set estado=PROGRAMADA y pacienteId=propio]
  B -->|Recepcionista| D[No permitir estado=REALIZADA]
  B -->|Doctor/Admin| E[Permitir edición completa]
  C --> F[Validar solapes médico/paciente]
  D --> F
  E --> F
  F -->|Sin solape| G[Verificar médico y paciente activos]
  F -->|Con solape| H[Error: conflicto horario]
  G --> I[Guardar cita]
  I --> J[201 Created]
```

## Migraciones Futuras
- Índices por (medicoId, fechaCita, horaInicio, horaFin) y (pacienteId, fechaCita, horaInicio, horaFin).
- Auditoría y soft delete uniforme.
- Externalizar URLs Feign a properties/perfiles; añadir tolerancia a fallos.

## Buenas Prácticas
- Validar propiedad/rol en controlador; reglas de horario en servicio.
- Mantener consistencia de estados; usar transacciones en cambios de estado.

## Flujo de Seguridad + Funcionamiento
- Entrada con JWT:
  - El filtro JwtTokenValidator valida el token y establece roles en el contexto.
- Autorización:
  - @PreAuthorize en controladores define acceso por rol (ADMIN, DOCTOR, PATIENT, RECEPTIONIST).
  - Propiedad:
    - PATIENT: solo accede a sus citas y solo puede cancelar.
    - DOCTOR: solo gestiona su propia agenda y puede marcar REALIZADA bajo condiciones.
    - RECEPTIONIST: no puede marcar REALIZADA.
- Funcionamiento general:
  - Controlador valida rol/propiedad y delega al servicio.
  - Servicio realiza validación de solapes y verifica estados de paciente/médico vía Feign.
  - Repositorio persiste cambios; las respuestas dependen de las reglas.

```mermaid
sequenceDiagram
  participant U as Usuario (JWT)
  participant FIL as JwtTokenValidator
  participant CTRL as CitaController
  participant SVC as CitaServiceImpl
  participant REPO as CitaRepository
  participant PAC as msvc-paciente
  participant MED as msvc-medico
  participant DIA as msvc-diagnostico
  U->>CTRL: POST/GET/PATCH /citas...
  CTRL->>FIL: Validar JWT
  FIL-->>CTRL: Contexto con roles
  CTRL->>CTRL: @PreAuthorize por rol
  CTRL->>CTRL: Validar propiedad (PATIENT/DOCTOR)
  CTRL->>SVC: Caso de uso (guardar/listar/cambiar estado)
  SVC->>REPO: existsConflict*/save/find*
  REPO-->>SVC: datos
  SVC->>PAC: GET paciente (Authorization)
  SVC->>MED: GET médico (Authorization)
  SVC->>DIA: GET diagnósticos por cita (Authorization)
  SVC-->>CTRL: Resultado
  CTRL-->>U: 200/201/403/404 según reglas
```
