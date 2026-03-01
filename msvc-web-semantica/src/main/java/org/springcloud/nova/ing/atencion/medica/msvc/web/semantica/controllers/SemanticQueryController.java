package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.controllers;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryRequest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryResponse;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.NaturalLanguageQueryService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SparqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/semantic")
public class SemanticQueryController {

    @Autowired
    private NaturalLanguageQueryService nlpService;

    @Autowired
    private SparqlQueryService sparqlService;

    @PostMapping("/nlp/query")
    public ResponseEntity<NaturalLanguageQueryResponse> queryNaturalLanguage(@RequestBody NaturalLanguageQueryRequest request) {
        return ResponseEntity.ok(nlpService.ejecutarConsulta(request));
    }

    @PostMapping("/sparql/query")
    public ResponseEntity<?> querySparql(@RequestBody Map<String, String> payload) {
        String query = payload.get("query");
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body("Falta el parametro 'query'");
        }
        try {
            List<Map<String, String>> resultados = sparqlService.ejecutarSelectSistemaCompleto(query);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
