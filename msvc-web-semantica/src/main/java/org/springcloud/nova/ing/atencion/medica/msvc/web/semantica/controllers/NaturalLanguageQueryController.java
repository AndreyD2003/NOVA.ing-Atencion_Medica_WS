package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.controllers;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryRequest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryResponse;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.NaturalLanguageQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/semantic/nl")
public class NaturalLanguageQueryController {

    @Autowired
    private NaturalLanguageQueryService naturalLanguageQueryService;

    @PostMapping("/query")
    public ResponseEntity<NaturalLanguageQueryResponse> ejecutarConsulta(@RequestBody NaturalLanguageQueryRequest request) {
        NaturalLanguageQueryResponse response = naturalLanguageQueryService.ejecutarConsulta(request);
        return ResponseEntity.ok(response);
    }
}
