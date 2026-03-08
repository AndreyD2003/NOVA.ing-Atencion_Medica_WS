package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.DiagnosticoSyncDTO;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.SyncRequestDTO;
import java.util.List;
import java.util.Map;

public interface ISemanticService {
    // Para las búsquedas en lenguaje natural
    List<Map<String, String>> buscarEnLenguajeNatural(String texto);

    // Para guardar datos de Citas (msvc-cita)
    void sincronizarAtencionMedica(SyncRequestDTO dto);

    // Para guardar datos de Diagnósticos (msvc-diagnostico)
    void sincronizarDiagnostico(DiagnosticoSyncDTO dto);

    // Para consultas SPARQL directas (opcional/avanzado)
    List<Map<String, String>> consultarSparql(String query);
}