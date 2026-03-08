# Casos de uso principales

Los casos de uso principales documentados para el sistema son:

1. Registrar médico y paciente.
2. Crear cita validando disponibilidad.
3. Consultar detalle agregado de cita.
4. Registrar diagnóstico sobre cita.
5. Consultar grafo RDF de una cita o del sistema.
6. Ejecutar consulta SPARQL o pregunta en lenguaje natural.

### Cómo se reparten por módulos

* `msvc-paciente` cubre registro y seguimiento del paciente.
* `msvc-medico` cubre gestión del médico y registro de diagnóstico.
* `msvc-cita` coordina agenda y detalle enriquecido.
* `msvc-diagnostico` concentra la evidencia diagnóstica.
* `msvc-web-semantica` expone el conocimiento clínico en formato grafo.
