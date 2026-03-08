package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load.DiagnosticoLoadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "msvc-diagnostico", url = "localhost:8082")
public interface DiagnosticoClient {
    @GetMapping("/diagnosticos")
    List<DiagnosticoLoadDTO> listarTodos();
}