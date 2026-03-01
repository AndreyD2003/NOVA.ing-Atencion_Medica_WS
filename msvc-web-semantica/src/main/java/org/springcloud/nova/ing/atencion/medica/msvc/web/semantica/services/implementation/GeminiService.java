package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class GeminiService {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_PROMPT = 
        "Eres un experto en Web Semántica y SPARQL. Tu tarea es convertir preguntas en lenguaje natural a consultas SPARQL validas para la siguiente ontología:\n" +
        "Prefijos:\n" +
        "PREFIX onto: <http://nova.ing/ontology/>\n" +
        "PREFIX am: <http://nova.ing/atencion-medica/>\n" +
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n\n" +
        "Clases Principales:\n" +
        "- onto:Paciente (propiedades: onto:nombres, onto:apellidos, onto:dni, onto:fechaNacimiento)\n" +
        "- onto:Medico (propiedades: onto:nombres, onto:apellidos, onto:especialidad)\n" +
        "- onto:Cita (propiedades: onto:fechaCita, onto:horaInicio, onto:motivo, onto:estado)\n" +
        "- onto:Diagnostico (propiedades: onto:descripcion, onto:tipoDiagnostico)\n" +
        "- onto:Horario (propiedades: onto:diaSemana, onto:horaInicio, onto:horaFin)\n\n" +
        "Relaciones:\n" +
        "- ?cita onto:paciente ?paciente\n" +
        "- ?cita onto:medico ?medico\n" +
        "- ?diagnostico onto:cita ?cita\n" +
        "- ?diagnostico onto:paciente ?paciente\n" +
        "- ?medico onto:tieneHorario ?horario\n\n" +
        "Instrucciones:\n" +
        "1. Genera SOLO el código SPARQL, sin explicaciones ni markdown (```sparql).\n" +
        "2. Usa siempre DISTINCT en el SELECT.\n" +
        "3. Si la pregunta es ambigua, genera la consulta más probable.\n" +
        "4. Para busquedas por texto (nombres), usa FILTER(CONTAINS(LCASE(?nombre), \"texto\")).\n" +
        "5. Mapea los días de la semana a Inglés en mayúsculas: lunes->MONDAY, martes->TUESDAY, etc.\n" +
        "6. Devuelve las variables ?s ?p ?o o las que sean relevantes para la pregunta.\n";

    public String generarSparql(String pregunta) {
        String fullPrompt = SYSTEM_PROMPT + "\nPregunta: " + pregunta + "\nSPARQL:";

        // Estructura del JSON para la API de Gemini
        // { "contents": [{ "parts": [{ "text": "..." }] }] }
        Map<String, Object> part = new HashMap<>();
        part.put("text", fullPrompt);
        
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(part);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);

        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(content);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String url = API_URL + "?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidateContent = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> candidateParts = (List<Map<String, Object>>) candidateContent.get("parts");
                    if (!candidateParts.isEmpty()) {
                        String text = (String) candidateParts.get(0).get("text");
                        return limpiarRespuesta(text);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error llamando a Gemini API: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private String limpiarRespuesta(String texto) {
        if (texto == null) return null;
        // Eliminar bloques de código markdown si existen
        String limpio = texto.replace("```sparql", "").replace("```", "").trim();
        return limpio;
    }
}
