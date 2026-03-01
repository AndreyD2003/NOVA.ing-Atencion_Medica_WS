package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.implementation;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryRequest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryResponse;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.NaturalLanguageQueryService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SparqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class NaturalLanguageQueryServiceImpl implements NaturalLanguageQueryService {

    @Autowired
    private SparqlQueryService sparqlQueryService;

    @Autowired
    private GeminiService geminiService;

    @Override
    public NaturalLanguageQueryResponse ejecutarConsulta(NaturalLanguageQueryRequest request) {
        NaturalLanguageQueryResponse response = new NaturalLanguageQueryResponse();
        if (request == null || request.getPregunta() == null) {
            response.setMensaje("La pregunta no puede ser nula.");
            response.setResultados(Collections.emptyList());
            return response;
        }

        String preguntaOriginal = request.getPregunta();
        response.setPregunta(preguntaOriginal);

        // 1. Usar Gemini para generar SPARQL
        String sparql = geminiService.generarSparql(preguntaOriginal);

        if (sparql == null || sparql.isEmpty()) {
            response.setMensaje("No se pudo interpretar la pregunta o generar una consulta válida.");
            response.setResultados(Collections.emptyList());
            return response;
        }

        response.setSparql(sparql);

        // 2. Ejecutar SPARQL generado
        try {
            List<Map<String, String>> resultados = sparqlQueryService.ejecutarSelectSistemaCompleto(sparql);
            response.setResultados(resultados);
            response.setMensaje("Consulta ejecutada correctamente.");
        } catch (Exception e) {
            response.setMensaje("Error ejecutando la consulta generada: " + e.getMessage());
            response.setResultados(Collections.emptyList());
        }

        return response;
    }
}
