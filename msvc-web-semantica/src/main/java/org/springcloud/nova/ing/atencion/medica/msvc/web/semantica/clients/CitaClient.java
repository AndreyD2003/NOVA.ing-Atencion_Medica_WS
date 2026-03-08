package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load.CitaLoadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "msvc-cita", url = "localhost:8081")
public interface CitaClient {
    @GetMapping("/citas")
    List<CitaLoadDTO> listarTodas();
}