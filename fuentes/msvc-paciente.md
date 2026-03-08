# msvc paciente

## Propósito

Gestionar pacientes y exponer operaciones clínicas relacionadas:

* CRUD de paciente.
* Consulta de citas del paciente.
* Consulta de historial médico.
* Cambio de estado de una cita por parte del paciente propietario.

## Entradas y salidas principales

### Entradas

* `CrearPacienteDto` para creación.
* `PacienteEntity` para edición.
* `Map<String, String>` con campo `estado` para cambio de estado de cita.
* Identificadores de recurso (`id`, `pacienteId`, `citaId`) en path params.

### Salidas

* `PacienteEntity` para operaciones CRUD.
* `List<Cita>` para consulta de citas.
* `List<Diagnostico>` para historial médico.
* Respuestas de error con `Map<String, String>` en validaciones y excepciones.

## Endpoints

* `GET /pacientes`
* `GET /pacientes/{id}`
* `GET /pacientes/{id}/citas`
* `GET /pacientes/{id}/historial-medico`
* `POST /pacientes`
* `PUT /pacientes/{id}`
* `DELETE /pacientes/{id}`
* `DELETE /pacientes/{id}/force`
* `POST /pacientes/agendar-cita`
* `PATCH /pacientes/{pacienteId}/citas/{citaId}/estado`

## Dependencias internas

* `PacienteController` → capa REST.
* `PacienteService` / `PacienteServiceImpl` → lógica de negocio.
* `PacienteRepository` → JPA.
* `PacienteEntity` + enums `GeneroPaciente`, `EstadoPaciente`.

## Dependencias externas

* `CitaClientRest` (`msvc-cita`):
  * listar citas por paciente,
  * crear cita,
  * obtener cita por id,
  * actualizar cita.
* `DiagnosticoClientRest` (`msvc-diagnostico`):
  * listar diagnósticos por paciente.

## Reglas y consideraciones importantes

* Soft delete de paciente: cambia estado a `INACTIVO`.
* Delete forzado: eliminación física por ID.
* Validaciones de entidad:
  * `dni` único con patrón `^[1-9][0-9]{7}$`,
  * `telefono` único con patrón `^9[0-9]{8}$`,
  * `email` único y formato válido.
* `cambiarEstadoCita` valida:
  * existencia de cita,
  * existencia de paciente,
  * correspondencia entre `pacienteId` y cita.

## Ejemplo de implementación

### Crear paciente

```json
POST /pacientes
{
  "paciente": {
    "nombres": "Lucia",
    "apellidos": "Ramos",
    "fechaNacimiento": "1999-01-01T00:00:00.000+00:00",
    "genero": "FEMENINO",
    "dni": "12345678",
    "telefono": "912345678",
    "email": "lucia.ramos@correo.com",
    "direccion": "Av. Central 123",
    "estado": "ACTIVO",
    "usuarioId": 10
  }
}
```

### Cambiar estado de cita del paciente

```json
PATCH /pacientes/1/citas/25/estado
{
  "estado": "CANCELADA"
}
```

## Casos de uso recomendados

* Portal de paciente con visualización de sus próximas citas.
* Consulta de historial de diagnósticos para seguimiento clínico.
* Cancelación/autogestión de citas del paciente.
