package org.nova.ing.springcloud.atencion.medica.msvc.paciente.clients;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.Diagnostico;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name = "msvc-diagnostico", url = "http://localhost:8082/diagnosticos")
public interface DiagnosticoClientRest {

    @GetMapping("/paciente/{id}")
    List<Diagnostico> listarPorPaciente(@PathVariable Long id);
}
