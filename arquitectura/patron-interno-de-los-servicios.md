# Patrón interno de los servicios

Los microservicios comparten una organización por capas.

Ese patrón facilita separar API, lógica de negocio, persistencia e integración remota.

### Estructura dominante

* `controllers`: API REST.
* `services`: reglas de negocio.
* `repositories`: acceso JPA.
* `models/entities`: persistencia.
* `models/dto`: contratos de intercambio.
* `clients`: integración Feign con otros microservicios.

### Qué significa en la práctica

#### `controllers`

Reciben la petición HTTP y delegan a servicios.

#### `services`

Aplican validaciones, coordinan dependencias y definen el flujo de negocio.

#### `repositories`

Aíslan consultas y persistencia.

#### `models/entities`

Representan datos persistidos por cada servicio.

#### `models/dto`

Evitan acoplar el contrato HTTP al modelo interno.

#### `clients`

Traen datos de otros servicios cuando hace falta enriquecer respuestas o validar reglas.

### Dónde se nota más este patrón

* en `msvc-cita`, para armar la cita enriquecida,
* en `msvc-paciente`, para historial y citas,
* en `msvc-medico`, para agenda y diagnósticos,
* en `msvc-web-semantica`, para construir grafos desde datos remotos.
