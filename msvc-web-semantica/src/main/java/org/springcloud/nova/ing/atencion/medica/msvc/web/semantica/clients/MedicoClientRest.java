package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.MedicoRemoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "msvc-medico", url = "http://localhost:8080/medicos")
public interface MedicoClientRest {

    @GetMapping
    List<MedicoRemoteDto> listar();
}
