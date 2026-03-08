package org.nova.ing.springcloud.atencion.medica.msvc.cita.clients;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.SyncRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Importante: El nombre del método debe ser 'sincronizar' para que coincida con el service
@FeignClient(name = "msvc-web-semantica", url = "localhost:8084")
public interface SemanticClientRest {

    @PostMapping("/api/v1/semantic/sync")
    void sincronizar(@RequestBody SyncRequestDTO dto);
}