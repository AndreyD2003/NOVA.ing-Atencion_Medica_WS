package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.controllers;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.RdfGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semantic/data")
public class SemanticDataController {

    @Autowired
    private RdfGraphService rdfGraphService;

    @PostMapping("/sync")
    public ResponseEntity<String> sincronizarDatos() {
        try {
            rdfGraphService.sincronizarDatosSistema();
            return ResponseEntity.ok("Sincronización de datos semánticos completada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error durante la sincronización: " + e.getMessage());
        }
    }
}
