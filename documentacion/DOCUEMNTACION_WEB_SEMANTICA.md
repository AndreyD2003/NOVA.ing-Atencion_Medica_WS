# Documentación del Proyecto Web Semántica

## Versión fácil de entender (no técnica)
- Qué hace: junta la información de pacientes, médicos, citas y diagnósticos en un “mapa” organizado para poder buscar y responder preguntas rápidamente.
- Para qué sirve: puedes preguntar cosas como “lista de los médicos” o “citas de hoy” y obtener respuestas claras, sin saber lenguajes complicados.
- Cómo lo logra:
  - Sincroniza datos de los otros módulos (Pacientes, Médicos, Citas, Diagnósticos).
  - Los guarda de forma ordenada para poder consultarlos.
  - Usa un asistente que entiende preguntas y las convierte en búsquedas.
- Dónde lo veo:
  - En el frontend: el botón “Sincronizar Datos” y el “Chat Semántico”.
  - Dashboard: muestra números reales (pacientes, médicos, citas del día, diagnósticos).

## Recorrido del usuario (paso a paso)
- Abrir la aplicación y revisar el Dashboard: verás la cantidad real de pacientes, médicos, citas de hoy y diagnósticos.
- Ir al Chat Semántico:
  - Pulsar “Sincronizar Datos” para asegurar que la información esté al día.
  - Escribir preguntas sencillas como:
    - “lista de los medicos”
    - “citas de hoy”
    - “diagnosticos por paciente 12”
  - Verás una tabla con resultados, y el sistema también muestra la “consulta SPARQL” que se usó detrás de escena.
- En las secciones de Pacientes, Médicos, Citas y Diagnósticos:
  - Crear, editar o eliminar registros.
  - Al sincronizar, los cambios se reflejan en el “mapa semántico” y las preguntas del chat usarán los datos actualizados.

## Ejemplos prácticos
- Pregunta: “lista de los medicos”
  - Respuesta esperada: tabla con nombres, apellidos y especialidad.
- Pregunta: “citas de hoy”
  - Respuesta esperada: tabla con fecha, hora y el paciente y médico asignados.
- Pregunta: “diagnosticos por paciente 12”
  - Respuesta esperada: lista de diagnósticos, tipo (presuntivo/definitivo) y fecha.
- SPARQL (para usuarios curiosos): “mostrar todo”
  - Enviar al endpoint de consultas: `SELECT * WHERE { ?s ?p ?o } LIMIT 10`
  - Verás tripletas (sujeto, predicado, objeto) que representan los datos.

## Cómo sincroniza (explicación breve)
- Primero limpia del “mapa semántico” cualquier dato viejo que ya no exista (para evitar que salgan elementos borrados).
- Luego trae datos frescos de los módulos (Pacientes, Médicos, Citas, Diagnósticos).
- Por último, guarda todo ordenado y listo para buscar.
- Si eliminas algo (ej. una cita), al sincronizar dejará de aparecer en las preguntas del chat.

## Si algo falla, ¿qué hago?
- Error al crear o editar: revisa que los campos coincidan con los valores esperados (por ejemplo, género “MASCULINO”/“FEMENINO”, especialidades en mayúsculas).
- Horas de cita: usa formato con segundos (ej. 14:30:00) si el sistema lo solicita.
- El chat muestra datos viejos: pulsa “Sincronizar Datos” y vuelve a preguntar.

## 1. Planeación
- Objetivo del proyecto
  - Unificar y enriquecer la información clínica de NOVA.ing Atención Médica en un Grafo de Conocimiento interoperable, consultable mediante SPARQL y accesible vía lenguaje natural.
- Alcance y justificación
  - Integración de los microservicios Paciente, Médico, Cita y Diagnóstico en un repositorio semántico persistente (Jena TDB2) para habilitar análisis y consultas avanzadas, con razonamiento OWL y generación de consultas SPARQL desde texto libre.
- Tecnologías semánticas seleccionadas
  - RDF para modelado de datos en tripletas sujeto–predicado–objeto.
  - RDFS/OWL para jerarquías, dominios/rangos y razonamiento.
  - SPARQL para consultas sobre el grafo.
  - Jena TDB2 como triplestore embebido y transaccional.
  - Opcional/Complementario: FHIR para futuros mapeos clínicos estándar (no implementado aún).

## 2. Requerimientos
- Fuentes de datos a integrar
  - Microservicios internos REST: Pacientes, Médicos, Citas, Diagnósticos.
  - Cada servicio expone endpoints de listado y detalle, consumidos por el MS de Web Semántica vía OpenFeign.
- Estándares de interoperabilidad
  - HTTP/REST para integración entre servicios.
  - RDF/OWL/SPARQL para interoperabilidad semántica.
  - Nombres de recursos estables mediante URIs propias: onto: y am:.
- Necesidades de los usuarios finales
  - Consultar información clínica y respuestas agregadas sin conocer SPARQL.
  - Realizar preguntas en lenguaje natural y obtener resultados estructurados.
  - Garantizar que los datos semánticos reflejan fielmente el estado operativo.

## 3. Diseño Conceptual
- Ontología inicial (clases, propiedades, relaciones)
  - Clases: Paciente, Medico, Cita, Diagnostico, Horario y conceptos auxiliares (Estados, Especialidades).
  - Propiedades: nombres, apellidos, dni, fechaNacimiento, genero, especialidad, estado, paciente, medico, cita, tieneHorario, horarioDe.
- Vocabularios y taxonomías a emplear
  - Propio (onto:) para dominio; uso de rdf:, rdfs:, xsd: para metamodelado.
  - Estructura T-Box en archivo OWL (ontología base) y A-Box en datos instanciados.
- Diagramas UML o mapas conceptuales
  - El diseño conceptual está reflejado en la ontología [nova-ontology.owl](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/resources/ontology/nova-ontology.owl).

## 4. Modelado Semántico
- Construcción de la ontología en RDF/OWL
  - La ontología base se carga automáticamente al inicializar el dataset si está vacío, mediante [RdfModelBuilder](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/semantic/RdfModelBuilder.java#L26-L45).
- Definición de reglas de inferencia
  - Se prepara un esquema RDFS/OWL y se activa el razonador OWL de Jena durante las consultas, ver [RdfGraphServiceImpl.aplicarRazonamientoOwl](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java#L207-L251).
- Mapeo de datos existentes hacia la ontología
  - Transformación de DTOs (Pacientes, Médicos, Citas, Diagnósticos) a recursos RDF con URIs estables basadas en BASE_IRI, ver [crearRecursoPaciente](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java#L253-L285), [crearRecursoMedico](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java#L288-L326), [crearRecursoCita](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java#L328-L355), [crearRecursoDiagnostico](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java#L357-L377).

## 5. Implementación
- Configuración de repositorios semánticos (triplestore)
  - Dataset Jena TDB2 inicializado en [RdfModelBuilder.init](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/semantic/RdfModelBuilder.java#L26-L45). Se establece namespace y carga ontología si el modelo está vacío.
- Integración con servicios web y APIs
  - Endpoints para sincronización y consultas:
    - [SemanticDataController.sync](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/controllers/SemanticDataController.java#L17-L25): POST /api/semantic/data/sync
    - [SemanticQueryController.nlp](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/controllers/SemanticQueryController.java#L27-L30): POST /api/semantic/nlp/query
    - [SemanticQueryController.sparql](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/controllers/SemanticQueryController.java#L32-L44): POST /api/semantic/sparql/query
- Desarrollo de consultas SPARQL
  - Las consultas se ejecutan en un “almacén de datos” optimizado y aplican reglas para obtener resultados más completos.
  - El asistente convierte tus preguntas (“lista de los médicos”) en búsquedas que el sistema entiende.

## 6. Validación y Pruebas
- Pruebas de interoperabilidad
  - Sincronización completa limpia: elimina instancias previas (URIs BASE_IRI) y recarga datos actuales, ver [RdfGraphServiceImpl.sincronizarDatosSistema](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java#L52-L115).
- Ejecución de consultas semánticas
  - Búsqueda directa: enviar una consulta estándar al endpoint de consultas.
  - Búsqueda por texto: escribir una pregunta sencilla y ver la respuesta.
- Evaluación de rendimiento y escalabilidad
  - Almacena grandes volúmenes de datos y responde rápido gracias a un diseño preparado para crecer.

## 7. Documentación Técnica
- Evidencias de cada etapa
  - Ontología base: [nova-ontology.owl](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/resources/ontology/nova-ontology.owl).
  - Construcción del dataset y namespaces: [RdfModelBuilder](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/semantic/RdfModelBuilder.java).
  - Servicios de sincronización y consulta: [RdfGraphServiceImpl](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica/src/main/java/org/springcloud/nova/ing/atencion/medica/msvc/web/semantica/services/implementation/RdfGraphServiceImpl.java).
- Manual de instalación y uso
  - Configurar `application.properties`:
    ```properties
    server.port=8085
    msvc.paciente.url=http://localhost:8083
    msvc.medico.url=http://localhost:8082
    msvc.cita.url=http://localhost:8081
    msvc.diagnostico.url=http://localhost:8084
    google.gemini.api-key=TU_API_KEY_AQUI
    ```
  - Iniciar MS semántico: `mvn spring-boot:run` en [msvc-web-semantica](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/msvc-web-semantica).
  - Endpoints: POST `/api/semantic/data/sync`, POST `/api/semantic/nlp/query`, POST `/api/semantic/sparql/query`.
- Registro de problemas y soluciones aplicadas
  - Datos eliminados que seguían apareciendo: se corrige limpiando antes de sincronizar.
  - Valores estandarizados (género, especialidad) para que no fallen las validaciones.
  - Formato de hora corregido para evitar errores.

## 8. Despliegue y Mantenimiento
- Publicación en entorno productivo
  - Contenerización futura recomendada (Docker) con volúmenes persistentes para TDB2 y variables seguras para API keys.
- Monitoreo de calidad de datos
  - Dashboard operativo en frontend con métricas de pacientes, médicos, citas de hoy y diagnósticos, ver [Dashboard.tsx](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/pages/Dashboard.tsx).
- Actualización de ontologías y vocabularios
  - Mantener control de versiones de `nova-ontology.owl`; usar URIs estables (ONTO/AM). Documentar cambios y re-sincronizar.

---

## Apéndice: Frontend (nova-frontend)
- Módulos principales de UI
  - Gestión de Pacientes: [PatientList.tsx](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/pages/PatientList.tsx)
  - Gestión de Médicos: [DoctorList.tsx](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/pages/DoctorList.tsx)
  - Citas: [AppointmentList.tsx](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/pages/AppointmentList.tsx)
  - Diagnósticos: [DiagnosisList.tsx](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/pages/DiagnosisList.tsx)
  - Chat Semántico: [SemanticChat.tsx](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/pages/SemanticChat.tsx)
- Servicios de API
  - Semántica: [semantic.service.ts](file:///c:/INGENIERIA%20DE%20SOFTWARE%20III/Web_Semantica/NOVA.ing-Atencion_Medica/nova-frontend/src/services/semantic.service.ts) — endpoints de NLP, SPARQL y sincronización.
  - Pacientes/Médicos/Citas/Diagnósticos: servicios CRUD que reflejan contratos backend.
- UX y consistencia semántica
  - Modales de confirmación y formularios con validación acorde a enums y formatos del backend.
  - Botón “Sincronizar Datos” en Chat para disparar `/api/semantic/data/sync`.

## Glosario simple
- Ontología: el “diccionario” que define qué entidades hay (Paciente, Médico, etc.) y cómo se relacionan.
- RDF/SPARQL: formatos y lenguaje para guardar y buscar en el “mapa semántico”.
- Sincronizar: actualizar el mapa semántico con los datos reales del sistema.
- Razonamiento: reglas que permiten deducir datos nuevos, por ejemplo “todo Médico es un Profesional de Salud”.

## Preguntas frecuentes (FAQ)
- ¿Por qué aparecen datos viejos en el chat?
  - Porque el mapa semántico guardó una versión anterior. Pulsa “Sincronizar Datos” y se limpia/actualiza.
- ¿Cómo busco sin saber SPARQL?
  - Usa el Chat Semántico. Escribe tu pregunta de forma natural.
- ¿Qué pasa si borro un paciente?
  - Al sincronizar, desaparece del mapa semántico y ya no saldrá en consultas.

---
**© 2026 NOVA.ing — Documentación del Proyecto Web Semántica**
