# Referencia de endpoints

### Consulta básica

#### `GET /diagnosticos`

Lista diagnósticos.

#### `GET /diagnosticos/{id}`

Obtiene un diagnóstico por ID.

### Consulta contextual

#### `GET /diagnosticos/cita/{id}`

Lista diagnósticos asociados a una cita.

#### `GET /diagnosticos/paciente/{id}`

Lista diagnósticos asociados a un paciente.

#### `GET /diagnosticos/con-detalle/{id}`

Devuelve `DiagnosticoDetalle` con recursos relacionados.

### Escritura

#### `POST /diagnosticos`

Crea un diagnóstico.

#### `PUT /diagnosticos/{id}`

Actualiza un diagnóstico.

### Borrado

#### `DELETE /diagnosticos/{id}`

Aplica soft delete usando `activo=false`.

#### `DELETE /diagnosticos/{id}/force`

Elimina físicamente el registro.

#### Ejemplo

```json
POST /diagnosticos
{
  "descripcion": "Infección respiratoria leve",
  "tipoDiagnostico": "PRESUNTIVO",
  "fechaDiagnostico": "2026-03-05T11:00:00.000+00:00",
  "citaId": 8,
  "pacienteId": 1
}
```
