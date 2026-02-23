package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.DiagnosticoRemoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "msvc-diagnostico", url = "http://localhost:8082/diagnosticos")
public interface DiagnosticoClientRest {

    @GetMapping
    List<DiagnosticoRemoteDto> listar();
}
