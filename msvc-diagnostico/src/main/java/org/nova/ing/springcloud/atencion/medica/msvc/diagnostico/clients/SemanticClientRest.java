package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.DiagnosticoSyncDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msvc-web-semantica", url = "localhost:8084")
public interface SemanticClientRest {
    @PostMapping("/api/v1/semantic/sync/diagnostico")
    void sincronizarDiagnostico(@RequestBody DiagnosticoSyncDTO dto);
}
