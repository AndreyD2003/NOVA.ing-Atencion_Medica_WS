# Datos, configuración y ejecución

### Requisitos locales

* JDK `25` disponible en `PATH`.
* Maven Wrapper en cada microservicio.
* MySQL en `localhost:3306`.
* PostgreSQL en `localhost:5432`.

### Motores y esquemas esperados

* MySQL:
  * `msvc_medicos`
  * `msvc_citas`
* PostgreSQL:
  * `msvc_pacientes`
  * `msvc_diagnosticos`
  * `msvc_web_semantica`

### Configuración actual de desarrollo

La configuración vive en `src/main/resources/application.properties` de cada servicio.

* MySQL: `root/admin`
* PostgreSQL: `postgres/admin`
* `spring.jpa.hibernate.ddl-auto=create-drop`
* `spring.jpa.show-sql=true`

{% hint style="warning" %}
`create-drop` elimina el esquema al reiniciar cada servicio.
{% endhint %}

### Levantar el proyecto

{% stepper %}
{% step %}
### Compilar desde la raíz

```bash
mvn clean install
```
{% endstep %}

{% step %}
### Ejecutar servicios

```bash
cd msvc-medico && mvnw.cmd spring-boot:run
cd msvc-cita && mvnw.cmd spring-boot:run
cd msvc-paciente && mvnw.cmd spring-boot:run
cd msvc-diagnostico && mvnw.cmd spring-boot:run
cd msvc-web-semantica && mvnw.cmd spring-boot:run
```
{% endstep %}

{% step %}
### Validar endpoints base

* Pacientes: `/pacientes`
* Médicos: `/medicos`
* Citas: `/citas`
* Diagnósticos: `/diagnosticos`
* Semántica: `/semantic/grafo/sistema/rdf?formato=TURTLE`
{% endstep %}
{% endstepper %}

### Pruebas por módulo

```bash
mvn test -pl msvc-paciente
mvn test -pl msvc-medico
mvn test -pl msvc-cita
mvn test -pl msvc-diagnostico
mvn test -pl msvc-web-semantica
```

### Recomendaciones ya identificadas

* Externalizar credenciales.
* Usar perfiles por ambiente.
* Cambiar DDL a `validate` o `none` fuera de desarrollo académico.
