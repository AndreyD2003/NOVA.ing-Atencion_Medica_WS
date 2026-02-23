package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NaturalLanguageQueryResponse {

    private String pregunta;

    private String sparql;

    private String mensaje;

    private List<Map<String, String>> resultados;
}
