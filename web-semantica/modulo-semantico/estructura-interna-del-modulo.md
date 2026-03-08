# Estructura interna del módulo

La carpeta de `msvc-web-semantica` separa claramente controladores, servicios, clientes y modelos.

### Estructura base

```
msvc-web-semantica/src/main/java/.../semantica/
├─ controllers/
├─ services/
│  └─ implementation/
├─ semantic/
├─ clients/
├─ models/
│  ├─ dto/
│  │  └─ remote/
│  └─ entities/
├─ repositories/
└─ MsvcWebSemanticaApplication.java
```

### Rol de cada bloque

#### `controllers/`

* `SemanticGraphController`
* `NaturalLanguageQueryController`

Exponen endpoints para grafo, RDF, SPARQL y lenguaje natural.

#### `services/implementation/`

* `SemanticGraphServiceImpl`
* `RdfGraphServiceImpl`
* `SparqlQueryServiceImpl`
* `NaturalLanguageQueryServiceImpl`

Implementan construcción de grafo, serialización, consulta SPARQL y traducción NL→SPARQL.

#### `semantic/`

* `RdfModelBuilder`

Convierte DTOs semánticos en recursos RDF.

#### `clients/`

* `PacienteClientRest`
* `MedicoClientRest`
* `CitaClientRest`
* `DiagnosticoClientRest`

Recolectan datos desde los microservicios operacionales.

#### `models/dto/remote/`

Incluye DTOs espejo de respuestas remotas.

#### `models/dto/`

Incluye `GrafoClinicoDto` y objetos de petición y respuesta del módulo.

#### `models/entities/` y `repositories/`

Contienen entidades semánticas persistibles y metadatos de ontología para crecimiento futuro.
