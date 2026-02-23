package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryRequest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryResponse;

public interface NaturalLanguageQueryService {

    NaturalLanguageQueryResponse ejecutarConsulta(NaturalLanguageQueryRequest request);
}
