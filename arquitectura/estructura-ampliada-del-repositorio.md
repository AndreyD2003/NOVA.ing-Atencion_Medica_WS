# Estructura ampliada del repositorio

### Estructura raíz documentada

```
Atencion_Medica_msvc_web_semantica_VF.2/
├─ README.md
├─ pom.xml
├─ run-frontend.bat
├─ NOVAingPruebas.json
├─ documentacion/
│  ├─ WebSemantica.md
├─ msvc-paciente/
├─ msvc-medico/
├─ msvc-cita/
├─ msvc-diagnostico/
├─ msvc-web-semantica/
└─ src/
```

### Propósito de los artefactos visibles

* `documentacion/` reúne documentación técnica por módulo y guías globales.
* `msvc-paciente/` gestiona pacientes e historial.
* `msvc-medico/` gestiona médicos, horarios y operaciones clínicas.
* `msvc-cita/` centraliza agenda, reglas de solape y detalle de cita.
* `msvc-diagnostico/` administra diagnósticos.
* `msvc-web-semantica/` construye el grafo semántico y expone consultas.
* `src/` contiene un `Main.java` de plantilla.

### Cuándo se usa cada módulo

* en operación clínica normal: `paciente`, `medico`, `cita` y `diagnostico`,
* en analítica y consulta de conocimiento: `msvc-web-semantica` consume los cuatro anteriores.
