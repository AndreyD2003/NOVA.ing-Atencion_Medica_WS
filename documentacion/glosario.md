# Glosario — NOVA Atención Médica

- JWT: Token JSON Web firmado; incluye claims como userId y roles (concepto histórico, no usado en la versión actual del sistema).
- Feign: Cliente HTTP declarativo para invocar endpoints de otros MSVCs.
- Propiedad: Regla de negocio que limita acceso/modificación a dueños del recurso.
- Solape: Intersección de rangos de horas en citas; inválido para médico/paciente.
- EstadoCita: PROGRAMADA, CANCELADA, REALIZADA.
- Soft delete: Desactivar en lugar de borrar; se usa activo=true en diagnósticos.
- @PreAuthorize: Anotación de Spring para verificar roles/authorities antes de ejecutar endpoints (concepto histórico, no aplicado en la versión actual del sistema).
- Repositorio: Capa de acceso a datos con consultas JPA/HQL.
- DTO: Objeto de transferencia para respuestas/solicitudes, evita exponer el modelo completo.
- Auditoría: Registro de cambios y actores; recomendable en futuras migraciones.
