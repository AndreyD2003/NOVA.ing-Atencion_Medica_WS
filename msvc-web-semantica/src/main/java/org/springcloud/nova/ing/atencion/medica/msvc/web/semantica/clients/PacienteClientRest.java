package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.PacienteRemoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "msvc-paciente", url = "http://localhost:8083/pacientes")
public interface PacienteClientRest {

    @GetMapping
    List<PacienteRemoteDto> listar();
}
