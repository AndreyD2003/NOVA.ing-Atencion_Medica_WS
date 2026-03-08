# Referencia de endpoints

### Consulta de médicos

#### `GET /medicos`

Lista médicos.

#### `GET /medicos/{id}`

Obtiene un médico por ID.

#### `GET /medicos/{id}/citas`

Devuelve la agenda del médico como `List<Cita>`.

### Escritura de médicos

#### `POST /medicos`

Crea un médico usando `CrearMedicoDto`.

#### `PUT /medicos/{id}`

Actualiza un médico usando `MedicoEntity`.

### Borrado

#### `DELETE /medicos/{id}`

Aplica soft delete cambiando el estado a `INACTIVO`.

#### `DELETE /medicos/{id}/force`

Elimina físicamente el registro.

### Operaciones clínicas

#### `POST /medicos/agendar-cita`

Agenda una cita desde el módulo médico.

#### `POST /medicos/registrar-diagnostico`

Registra un diagnóstico vinculado a una cita.

#### Ejemplo

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
