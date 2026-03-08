package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.SyncRequestDTO;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.BulkSyncService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.ISemanticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/semantic")
@RequiredArgsConstructor
@Slf4j
public class SemanticController {

    private final ISemanticService semanticService;

    /**
     * Búsqueda en Lenguaje Natural.
     * Ejemplo: GET /api/v1/semantic/buscar?texto=citas de cardiologia del paciente 12345678
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Map<String, String>>> buscar(@RequestParam String texto) {
        log.info("Request recibida para búsqueda semántica: '{}'", texto);
        List<Map<String, String>> resultados = semanticService.buscarEnLenguajeNatural(texto);
        return ResponseEntity.ok(resultados);
    }

    @Autowired
    private BulkSyncService bulkSyncService;

    @PostMapping("/bulk-load")
    public ResponseEntity<String> cargarTodo() {
        bulkSyncService.sincronizarTodoElSistema();
        return ResponseEntity.ok("Proceso de carga masiva iniciado exitosamente.");
    }

    /**
     * Sincronización de datos desde otros Microservicios.
     * Este endpoint será llamado por msvc-cita o un orquestador cuando ocurra una atención.
     */
    @PostMapping("/sync")
    public ResponseEntity<String> sincronizar(@RequestBody SyncRequestDTO dto) {
        log.info("Request recibida para sincronizar Cita ID: {}", dto.getCitaId());
        try {
            semanticService.sincronizarAtencionMedica(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Datos integrados correctamente en el Grafo de Fuseki.");
        } catch (Exception e) {
            log.error("Error en sincronización: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al sincronizar con el motor semántico.");
        }
    }

    /**
     * Ejecución de consultas SPARQL directas (Para administración o debug).
     */
    @PostMapping("/sparql")
    public ResponseEntity<List<Map<String, String>>> ejecutarSparql(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(semanticService.consultarSparql(query));
    }
}