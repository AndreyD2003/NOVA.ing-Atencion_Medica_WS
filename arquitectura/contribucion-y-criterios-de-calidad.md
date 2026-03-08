# Contribución y criterios de calidad

### Flujo de contribución

{% stepper %}
{% step %}
### Crear rama de trabajo

Usar ramas `feature/*`, `fix/*` o `docs/*`.
{% endstep %}

{% step %}
### Implementar cambios

Mantener separación por dominio.
{% endstep %}

{% step %}
### Ejecutar compilación y pruebas

Validar los módulos afectados.
{% endstep %}

{% step %}
### Verificar contratos

Revisar contratos HTTP entre servicios involucrados.
{% endstep %}

{% step %}
### Actualizar documentación

Mantener actualizados `README.md` y `documentacion/*.md`.
{% endstep %}
{% endstepper %}

### Criterios de calidad

* mantener cohesión del dominio por microservicio,
* evitar acoplamiento por base de datos entre servicios,
* preferir DTOs para integración remota,
* mantener consistencia de enums de estado,
* documentar cualquier endpoint o contrato nuevo.
