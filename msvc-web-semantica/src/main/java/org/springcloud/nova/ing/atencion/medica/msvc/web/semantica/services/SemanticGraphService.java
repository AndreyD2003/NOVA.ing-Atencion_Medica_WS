package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.GrafoClinicoDto;

public interface SemanticGraphService {

    GrafoClinicoDto construirGrafoPorCitaId(Long citaId);
}
