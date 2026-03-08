# Comunicación y contratos

La comunicación entre servicios es síncrona y usa HTTP con OpenFeign.

La integración principal ocurre por composición de respuestas. El ejemplo más claro es la cita enriquecida: cita + paciente + médico + diagnósticos.

### Patrón de integración

* Cada servicio mantiene su propio dominio.
* Los datos agregados se arman en tiempo de consulta.
* La capa semántica consume los servicios operacionales y construye un modelo unificado.

### Contratos de integración relevantes

#### `msvc-cita` consume

* `GET /pacientes/{id}`
* `GET /medicos/{id}`
* `GET /diagnosticos/cita/{id}`

#### `msvc-paciente` consume

* `GET /citas/paciente/{id}`
* `GET /diagnosticos/paciente/{id}`

#### `msvc-medico` consume

* `GET /citas/medico/{id}`
* `GET /citas/{id}`
* `POST /diagnosticos`

#### `msvc-diagnostico` consume

* `GET /citas/{id}`
* `GET /pacientes/{id}`

#### `msvc-web-semantica` consume

* listados de pacientes, médicos, citas y diagnósticos,
* detalle de cita enriquecida.

### Qué vistas se agregan

* `msvc-cita` devuelve una vista enriquecida de cita.
* `msvc-diagnostico` devuelve una vista detallada con cita y paciente.
* `msvc-paciente` arma historial clínico con diagnósticos.
* `msvc-web-semantica` crea grafos por cita o del sistema completo.

### Riesgos técnicos actuales

* La integración síncrona propaga latencia.
* No hay fallback ni circuit breaker.
* Hay `try/catch` silenciosos en agregaciones.
* No hay autenticación o autorización activa en controladores.
