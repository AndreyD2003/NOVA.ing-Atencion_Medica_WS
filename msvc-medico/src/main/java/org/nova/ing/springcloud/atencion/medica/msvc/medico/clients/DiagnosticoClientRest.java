package org.nova.ing.springcloud.atencion.medica.msvc.medico.clients;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.Diagnostico;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@FeignClient(name = "msvc-diagnostico", url = "http://localhost:8082/diagnosticos")
public interface DiagnosticoClientRest {

    @PostMapping
    Diagnostico crear(@RequestBody Diagnostico diagnostico);

    @GetMapping("/cita/{id}")
    List<Diagnostico> listarPorCita(@PathVariable Long id);

    @GetMapping("/paciente/{id}")
    List<Diagnostico> listarPorPaciente(@PathVariable Long id);
}
