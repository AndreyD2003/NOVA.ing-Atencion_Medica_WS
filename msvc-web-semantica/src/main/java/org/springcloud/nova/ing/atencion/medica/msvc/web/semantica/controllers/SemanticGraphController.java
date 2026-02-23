package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.controllers;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.GrafoClinicoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.RdfGraphService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SemanticGraphService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SparqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/semantic")
public class SemanticGraphController {

    @Autowired
    private SemanticGraphService semanticGraphService;

    @Autowired
    private RdfGraphService rdfGraphService;

    @Autowired
    private SparqlQueryService sparqlQueryService;

    @GetMapping("/grafo/cita/{id}")
    public ResponseEntity<GrafoClinicoDto> obtenerGrafoPorCita(@PathVariable Long id) {
        GrafoClinicoDto grafo = semanticGraphService.construirGrafoPorCitaId(id);
        return ResponseEntity.ok(grafo);
    }

    @GetMapping("/grafo/cita/{id}/rdf")
    public ResponseEntity<String> obtenerGrafoRdfPorCita(@PathVariable Long id,
                                                         @RequestParam(name = "formato", defaultValue = "TURTLE") String formato) {
        String rdf = rdfGraphService.serializarModeloPorCitaId(id, formato);
        MediaType mediaType = resolveMediaType(formato);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .body(rdf);
    }

    @PostMapping("/grafo/cita/{id}/sparql")
    public ResponseEntity<List<Map<String, String>>> ejecutarSparqlSobreCita(@PathVariable Long id,
                                                                             @RequestBody String consulta) {
        List<Map<String, String>> resultado = sparqlQueryService.ejecutarSelectPorCita(id, consulta);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/grafo/sistema/rdf")
    public ResponseEntity<String> obtenerGrafoRdfSistemaCompleto(@RequestParam(name = "formato", defaultValue = "TURTLE") String formato) {
        String rdf = rdfGraphService.serializarModeloSistemaCompleto(formato);
        MediaType mediaType = resolveMediaType(formato);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .body(rdf);
    }

    @PostMapping("/grafo/sistema/sparql")
    public ResponseEntity<List<Map<String, String>>> ejecutarSparqlSobreSistemaCompleto(@RequestBody String consulta) {
        List<Map<String, String>> resultado = sparqlQueryService.ejecutarSelectSistemaCompleto(consulta);
        return ResponseEntity.ok(resultado);
    }

    private MediaType resolveMediaType(String formato) {
        if (formato == null) {
            return MediaType.valueOf("text/turtle");
        }
        String f = formato.toUpperCase();
        switch (f) {
            case "RDFXML":
            case "RDF/XML":
                return MediaType.valueOf("application/rdf+xml");
            case "JSONLD":
            case "JSON-LD":
                return MediaType.valueOf("application/ld+json");
            case "TTL":
            case "TURTLE":
            default:
                return MediaType.valueOf("text/turtle");
        }
    }
}
