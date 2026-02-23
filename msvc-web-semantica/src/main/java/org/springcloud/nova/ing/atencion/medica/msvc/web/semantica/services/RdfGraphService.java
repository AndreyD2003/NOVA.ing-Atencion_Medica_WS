package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import org.apache.jena.rdf.model.Model;

public interface RdfGraphService {

    Model construirModeloPorCitaId(Long citaId);

    String serializarModeloPorCitaId(Long citaId, String formato);

    Model construirModeloSistemaCompleto();

    String serializarModeloSistemaCompleto(String formato);
}

