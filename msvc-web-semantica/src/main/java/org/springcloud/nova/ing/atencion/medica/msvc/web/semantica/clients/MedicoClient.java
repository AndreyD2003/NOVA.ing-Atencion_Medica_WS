package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load.MedicoLoadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "msvc-medico", url = "localhost:8080")
public interface MedicoClient {
    @GetMapping("/medicos")
    List<MedicoLoadDTO> listarTodos();
}