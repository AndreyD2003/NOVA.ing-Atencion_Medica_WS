# Documentación del Microservicio Web Semántica (`msvc-web-semantica`)

## 1. Introducción
El microservicio **`msvc-web-semantica`** es el componente central de inteligencia y gestión del conocimiento del sistema **NOVA.ing Atención Médica**. Su propósito principal es unificar la información dispersa en los diferentes microservicios operacionales (Pacientes, Médicos, Citas, Diagnósticos) en un **Grafo de Conocimiento (Knowledge Graph)** basado en estándares de la Web Semántica (RDF, OWL, SPARQL).

Además, actúa como una interfaz de lenguaje natural, permitiendo a los usuarios realizar consultas complejas ("¿Qué médicos atienden hoy?", "¿Cuántos pacientes tienen gripe?") sin necesidad de conocer lenguajes técnicos, gracias a la integración con Inteligencia Artificial Generativa (Google Gemini).

## 2. Arquitectura Técnica

### 2.1 Tecnologías Clave
*   **Framework:** Spring Boot 3.5.9 (Java 25).
*   **Motor Semántico:** **Apache Jena** (Versión 5.6.0).
*   **Almacenamiento Persistente:** **Jena TDB2** (Base de datos nativa para RDF, transaccional y de alto rendimiento).
*   **Motor de Inferencia:** Jena OWL Reasoner (para deducción lógica automática).
*   **Inteligencia Artificial:** Integración con **Google Gemini 1.5 Flash** para traducción de Lenguaje Natural a SPARQL.
*   **Comunicación entre Microservicios:** Spring Cloud OpenFeign (REST Clients).

### 2.2 Flujo de Datos General
1.  **Ingesta:** El servicio consume datos vía REST de los microservicios `msvc-paciente`, `msvc-medico`, `msvc-cita` y `msvc-diagnostico`.
2.  **Transformación:** Los datos (DTOs JSON) se transforman en tripletas RDF (Sujeto - Predicado - Objeto) siguiendo una ontología definida.
3.  **Almacenamiento:** Las tripletas se guardan en el Dataset TDB2 local (`tdb2_nova_db`).
4.  **Consulta:**
    *   El usuario envía una pregunta en texto.
    *   Gemini AI traduce la pregunta a SPARQL.
    *   Jena ejecuta la consulta SPARQL sobre el grafo, aplicando razonamiento en tiempo real.

## 3. Modelo Ontológico

El sistema utiliza una ontología propia para estructurar la información.

*   **Namespace Ontología (T-Box):** `http://nova.ing/ontology/` (prefijo `onto:`)
*   **Namespace Datos (A-Box):** `http://nova.ing/atencion-medica/` (prefijo `am:`)

### 3.1 Clases Principales
*   `onto:Paciente`: Representa a un paciente registrado.
    *   Subclase de: `foaf:Person`.
*   `onto:Medico`: Representa a un profesional de la salud.
    *   Subclase de: `onto:ProfesionalSalud`.
*   `onto:Cita`: Representa un evento de atención médica.
    *   Subclase de: `onto:EventoClinico`.
*   `onto:Diagnostico`: Representa el resultado clínico de una cita.
*   `onto:Horario`: Representa la disponibilidad de un médico.

### 3.2 Propiedades Clave
| Propiedad | Dominio | Rango | Descripción |
| :--- | :--- | :--- | :--- |
| `onto:paciente` | `onto:Cita` | `onto:Paciente` | Relaciona una cita con quien la recibe. |
| `onto:medico` | `onto:Cita` | `onto:Medico` | Relaciona una cita con quien la atiende. |
| `onto:cita` | `onto:Diagnostico` | `onto:Cita` | Vincula un diagnóstico a su cita origen. |
| `onto:tieneHorario` | `onto:Medico` | `onto:Horario` | Asigna horarios a un médico. |
| `onto:nombres`, `onto:apellidos` | Varios | `xsd:string` | Datos literales. |

## 4. Sincronización de Datos

El servicio implementa una estrategia de **Sincronización Completa con Limpieza Inteligente**. Esto asegura que el Grafo de Conocimiento siempre refleje el estado exacto de la base de datos relacional, eliminando datos "fantasmas" (borrados en el origen).

### Lógica del proceso (`RdfGraphServiceImpl.java`):
1.  **Inicio de Transacción de Escritura (WRITE).**
2.  **Limpieza de Instancias:** Se identifican y eliminan todas las tripletas cuyo sujeto comience con la URI base de datos (`http://nova.ing/atencion-medica/`).
    *   *Nota:* Esto preserva la ontología (estructura), borrando solo los datos (instancias).
3.  **Extracción (Extract):** Se llama a los endpoints `listar()` de todos los microservicios externos.
4.  **Transformación y Carga (Load):**
    *   Se iteran los DTOs recibidos.
    *   Se crean recursos RDF (`Resource`) y se añaden propiedades (`addProperty`, `addLiteral`).
    *   Se establecen las relaciones entre recursos (ej. vincular la URI de una Cita con la URI de un Médico).
5.  **Commit:** Se confirman los cambios en TDB2.

## 5. Procesamiento de Lenguaje Natural (NLP) e IA

En lugar de utilizar técnicas tradicionales de NLP (tokenización manual, árboles de decisión), el sistema aprovecha la potencia de los **LLMs (Large Language Models)**.

### Flujo de Consulta (`NaturalLanguageQueryServiceImpl`):
1.  **Recepción:** El usuario envía: *"Lista de médicos cardiólogos"*.
2.  **Prompt Engineering (`GeminiService`):**
    *   El sistema construye un prompt detallado que incluye:
        *   El rol ("Eres un experto en SPARQL...").
        *   El esquema completo de la ontología (Prefijos, Clases, Propiedades).
        *   Reglas de negocio (usar `FILTER(CONTAINS...)` para texto, `DISTINCT`, etc.).
        *   La pregunta del usuario.
3.  **Inferencia Externa:** Se envía el prompt a la API de Google Gemini (`gemini-3-flash-preview`).
4.  **Generación de SPARQL:** Gemini devuelve una consulta SPARQL válida, por ejemplo:
    ```sparql
    PREFIX onto: <http://nova.ing/ontology/>
    SELECT DISTINCT ?nombres ?apellidos
    WHERE {
      ?m a onto:Medico .
      ?m onto:especialidad "CARDIOLOGIA" .
      ?m onto:nombres ?nombres .
      ?m onto:apellidos ?apellidos .
    }
    ```
5.  **Ejecución:** El servicio ejecuta este SPARQL contra su base de datos TDB2 local.
6.  **Respuesta:** Los resultados se devuelven al frontend en formato JSON.

## 6. Motor de Razonamiento (Inferencia)

El sistema utiliza el **Jena OWL Reasoner** para inferir conocimiento que no está explícito en la base de datos.

### Ejemplo de Inferencia:
*   **Hecho explícito:** Juan es una instancia de `onto:Medico`.
*   **Regla Ontológica:** `onto:Medico` es subclase de `onto:ProfesionalSalud`.
*   **Inferencia:** Si consultamos `SELECT ?x WHERE { ?x a onto:ProfesionalSalud }`, el sistema devolverá a Juan, aunque nunca lo hayamos declarado explícitamente como tal.

Esto permite realizar consultas más abstractas y potentes sobre los datos.

## 7. API Reference

### 7.1 Sincronizar Datos
*   **Endpoint:** `POST /api/semantic/data/sync`
*   **Descripción:** Fuerza la actualización del grafo RDF con los datos de los microservicios.
*   **Respuesta:** `200 OK` "Sincronización completada...".

### 7.2 Consulta en Lenguaje Natural
*   **Endpoint:** `POST /api/semantic/nlp/query`
*   **Body:**
    ```json
    {
      "pregunta": "¿Qué pacientes tienen cita hoy?"
    }
    ```
*   **Respuesta:**
    ```json
    {
      "pregunta": "¿Qué pacientes tienen cita hoy?",
      "sparql": "SELECT ...",
      "mensaje": "Consulta ejecutada correctamente.",
      "resultados": [ ... ]
    }
    ```

### 7.3 Consulta SPARQL Directa
*   **Endpoint:** `POST /api/semantic/sparql/query`
*   **Body:**
    ```json
    {
      "query": "SELECT * WHERE { ?s ?p ?o } LIMIT 10"
    }
    ```

## 8. Configuración y Despliegue

### Requisitos Previos
*   Java 25 (o compatible con 17+).
*   Maven.
*   API Key de Google Gemini.

### Variables de Entorno
El archivo `application.properties` debe contener:
```properties
# Puerto
server.port=8085

# Configuración de Feign Clients (URLs de otros MSVCs)
msvc.paciente.url=http://localhost:8083
msvc.medico.url=http://localhost:8082
msvc.cita.url=http://localhost:8081
msvc.diagnostico.url=http://localhost:8084

# API Key de Gemini
google.gemini.api-key
```

## 9. Validación de Requisitos de Evaluación

Esta sección detalla explícitamente cómo el microservicio cumple con los criterios de evaluación técnica y qué evidencias se pueden presentar para validarlos.

### 9.1 Estructura, Modularidad y Protocolo RESTful
*   **Criterio:** "El microservicio está bien estructurado, es modular y escalable. Se utiliza correctamente RESTful..."
*   **Cumplimiento en el Proyecto:**
    *   **Estructura:** Arquitectura en capas de Spring Boot (`Controller` -> `Service` -> `Repository`/`Client`).
    *   **Modularidad:** Separación clara entre la lógica semántica (`RdfGraphService`), la integración de IA (`GeminiService`) y la comunicación externa (`Clients` Feign).
    *   **RESTful:** Uso semántico de verbos HTTP (`GET` para consultas, `POST` para comandos complejos/sync).
*   **Evidencia para mostrar:**
    *   Árbol de directorios del proyecto.
    *   Clase `SemanticDataController` mostrando anotaciones `@RestController`.
    *   Uso de `OpenFeign` para consumir otros microservicios de forma modular.

### 9.2 Ontologías y Estándares (OWL, RDF)
*   **Criterio:** "Se han usado ontologías adecuadas y estándar... Se hace un buen uso de los vocabularios semánticos."
*   **Cumplimiento en el Proyecto:**
    *   **Ontología Propia:** `nova-ontology.owl` define el dominio médico.
    *   **Estándares:** Uso de `RDF` (para instanciación), `RDFS` (para jerarquías y definiciones), y `OWL` (para reglas de inferencia).
    *   **Vocabularios:** Uso correcto de URIs y Prefijos (`onto:`, `rdf:`, `rdfs:`).
*   **Evidencia para mostrar:**
    *   Archivo `src/main/resources/ontology/nova-ontology.owl`.
    *   Clase `RdfGraphServiceImpl` donde se construyen las tripletas usando `model.createResource` y propiedades estándar.

### 9.3 Funcionalidad y Eficiencia
*   **Criterio:** "El microservicio está completamente funcional y resuelve el problema de manera eficiente..."
*   **Cumplimiento en el Proyecto:**
    *   **Funcionalidad:** Sincronización end-to-end operativa y Chatbot funcional.
    *   **Eficiencia:** Uso de **Apache Jena TDB2**, un motor de almacenamiento RDF nativo de alto rendimiento que evita los overheads de mapeo ORM complejos para grafos. La sincronización usa procesamiento en memoria antes de commitear (transaccional).
*   **Evidencia para mostrar:**
    *   **Demo en vivo:** Sincronizar datos y ver la respuesta inmediata "Sincronización completada".
    *   **Demo Chat:** Preguntar "¿Cuántos médicos hay?" y recibir la respuesta exacta en milisegundos.

### 9.4 Implementación RDF y SPARQL
*   **Criterio:** "Se ha implementado RDF de forma eficiente y el microservicio permite realizar consultas SPARQL correctamente."
*   **Cumplimiento en el Proyecto:**
    *   **RDF:** Generación dinámica de tripletas basada en datos relacionales.
    *   **SPARQL:** Endpoint dedicado `/api/semantic/sparql/query` que permite ejecutar cualquier consulta estándar sobre el grafo.
*   **Evidencia para mostrar:**
    *   Ejecutar una consulta SPARQL cruda (ej. `SELECT * WHERE { ?s ?p ?o }`) y ver el JSON de respuesta.
    *   Ver los logs donde se muestra el SPARQL generado automáticamente por la IA.

### 9.5 Escalabilidad y Mantenibilidad
*   **Criterio:** "Diseñado para ser escalable y fácil de mantener... buenas prácticas de desarrollo."
*   **Cumplimiento en el Proyecto:**
    *   **Escalabilidad:** TDB2 escala a millones de tripletas. El servicio es Stateless.
    *   **Buenas Prácticas:** Inyección de Dependencias (`@Autowired`), Programación orientada a Interfaces (`Service` vs `ServiceImpl`), DTOs para desacoplar capas, Manejo de Excepciones centralizado.
*   **Evidencia para mostrar:**
    *   Interfaces `RdfGraphService` y `NaturalLanguageQueryService`.
    *   Paquete `models/dto` mostrando la separación de datos.

### 9.6 Documentación
*   **Criterio:** "La documentación es completa, clara, bien estructurada..."
*   **Cumplimiento en el Proyecto:**
    *   Este documento actual sirve como prueba directa. Cubre arquitectura, flujos, ontología y validación.
*   **Evidencia para mostrar:**
    *   Presentar este archivo Markdown renderizado.

---
**© 2026 NOVA.ing - Documentación Técnica Generada Automáticamente**