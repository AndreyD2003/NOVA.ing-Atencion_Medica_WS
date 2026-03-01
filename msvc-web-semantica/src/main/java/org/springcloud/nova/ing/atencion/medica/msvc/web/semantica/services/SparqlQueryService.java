package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import java.util.List;
import java.util.Map;

public interface SparqlQueryService {

    List<Map<String, String>> ejecutarSelectSistemaCompleto(String sparql);
}

