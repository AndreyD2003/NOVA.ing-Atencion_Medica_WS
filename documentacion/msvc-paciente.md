# MSVC Paciente — Documentación

> Nota de versión actual: este servicio ya no utiliza JWT ni validación de roles en código; las referencias a JwtUtils, filtros de seguridad o @PreAuthorize son históricas.

## Propósito
- Gestiona el ciclo de vida del paciente y expone consultas relacionadas (citas, historial médico).
- Aplica validaciones de formato y unicidad sobre datos sensibles.

## Estructura Interna
- Controller: [PacienteController](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-paciente/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/paciente/controllers/PacienteController.java)
- Service: PacienteService (interfaz e implementación, ver paquete services)
- Repository: PacienteRepository (consultas de dominio)
- Entidad: [PacienteEntity](file:///d:/IngSoftware3/NOVA_ing-AtencionMedica_V.5_End/msvc-paciente/src/main/java/org/nova/ing/springcloud/atencion/medica/msvc/paciente/models/entities/PacienteEntity.java)
- Enums: EstadoPaciente, GeneroPaciente
- Seguridad: reglas por rol en controladores; JwtUtils para extraer userId del token.
- Feign Clients: hacia MSVC Cita y Diagnóstico para agregados.

## Ciclo de Funcionamiento por Clase
- PacienteController:
  - Restringe lectura por rol/propiedad (paciente solo accede a su perfil).
  - Delegaciones a servicio para crear/editar/eliminar y obtener citas/historial.
- PacienteService:
  - Reglas de negocio y llamadas a MSVCs remotos para componer vistas (citas, historial).
- PacienteRepository:
  - Consultas por usuarioId y filtros de negocio.
- PacienteEntity:
  - Validaciones de formato: DNI, teléfono, email; unicidad; campos obligatorios.
- JwtUtils:
  - Obtiene userId del token para comprobar propiedad de recursos.

## Flujo de Funcionamiento
```mermaid
sequenceDiagram
  participant C as Cliente
  participant PAC as PacienteController
  participant S as PacienteService
  participant R as PacienteRepository
  C->>PAC: GET /pacientes/{id}
  PAC->>PAC: validar rol/propiedad con JWT
  PAC->>S: porId(id)
  S->>R: findById
  R-->>S: paciente
  S-->>PAC: paciente
  PAC-->>C: 200/403 según reglas
```

## Catálogo de Endpoints
- GET /pacientes (ADMIN, RECEPTIONIST)
- GET /pacientes/{id} (propiedad aplicada para PATIENT)
- GET /pacientes/usuario/{usuarioId}
- GET /pacientes/{id}/citas (propiedad aplicada para PATIENT)
- POST /pacientes (ADMIN)
- PUT /pacientes/{id}
- DELETE /pacientes/{id}
- DELETE /pacientes/{id}/force (ADMIN)
- POST /pacientes/agendar-cita (PATIENT, ADMIN)
- PATCH /pacientes/{pacienteId}/citas/{citaId}/estado (PATIENT propietario)
- GET /pacientes/{id}/historial-medico

## Reglas de Validación
- DNI: patrón ^[1-9][0-9]{7}$ y único.
- Teléfono: patrón ^9[0-9]{8}$ y único.
- Email: formato Email y único.
- Campos obligatorios para nombres, apellidos, dirección, fecha de nacimiento, género y estado.

## Diagrama ER
```mermaid
erDiagram
  PACIENTE {
    Long id
    String dni
    String telefono
    String email
    EstadoPaciente estado
    Long usuarioId
  }
```

## Diagramas Adicionales
- Secuencia: Cambiar estado de cita por paciente propietario
```mermaid
sequenceDiagram
  participant P as Paciente
  participant PAC as PacienteController
  participant S as PacienteService
  P->>PAC: PATCH /pacientes/{pid}/citas/{cid}/estado
  PAC->>PAC: Validar rol PATIENT y propiedad (usuarioId)
  PAC->>S: cambiarEstadoCita(cid, pid, estado)
  S-->>PAC: citaActualizada
  PAC-->>P: 200 OK
```

- Flujo: Validaciones de datos de Paciente
```mermaid
flowchart TD
  A[Datos de paciente] --> B{DNI válido y único?}
  B -->|No| X[Error 400]
  B -->|Sí| C{Teléfono válido y único?}
  C -->|No| X
  C -->|Sí| D{Email válido y único?}
  D -->|No| X
  D -->|Sí| E[Guardar/Actualizar Paciente]
  E --> F[201/200 OK]
```

## Migraciones Futuras
- Índices por dni, telefono, email.
- Auditoría y trazabilidad de cambios (who/when).
- Separar datos sensibles y cifrado en repositorio si aplica.

## Buenas Prácticas
- Validar propiedad estrictamente en controladores.
- Reutilizar DTOs para respuestas públicas evitando exponer todo el modelo.

## Flujo de Seguridad + Funcionamiento
- Entrada con JWT:
  - El filtro JwtTokenValidator valida token y roles en el contexto.
- Autorización:
  - @PreAuthorize aplica restricciones (ADMIN, RECEPTIONIST, PATIENT).
  - Propiedad:
    - PATIENT: solo accede a su perfil y recursos asociados (citas/historial), validando usuarioId.
- Funcionamiento general:
  - Controlador valida rol/propiedad y delega al servicio.
  - Servicio consulta el repositorio y compone vistas; para citas/historial puede invocar Feign a Cita/Diagnóstico.

```mermaid
sequenceDiagram
  participant U as Usuario (JWT)
  participant FIL as JwtTokenValidator
  participant CTRL as PacienteController
  participant SVC as PacienteService
  participant REPO as PacienteRepository
  participant CIT as msvc-cita
  participant DIA as msvc-diagnostico
  U->>CTRL: GET/POST/PATCH /pacientes...
  CTRL->>FIL: Validar JWT
  FIL-->>CTRL: Contexto con roles
  CTRL->>CTRL: @PreAuthorize por rol
  CTRL->>CTRL: Validar propiedad (usuarioId)
  CTRL->>SVC: Caso de uso (porId/crear/editar/cambiar estado de cita)
  SVC->>REPO: find*/save*
  REPO-->>SVC: datos
  SVC->>CIT: GET/POST citas (Authorization)
  SVC->>DIA: GET historial (Authorization)
  SVC-->>CTRL: Resultado
  CTRL-->>U: 200/201/403/404 según reglas
```
