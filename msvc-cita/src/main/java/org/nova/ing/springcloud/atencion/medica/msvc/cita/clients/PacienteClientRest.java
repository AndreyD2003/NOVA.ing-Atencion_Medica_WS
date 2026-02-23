package org.nova.ing.springcloud.atencion.medica.msvc.cita.clients;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.Paciente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-paciente", url = "http://localhost:8083/pacientes")
public interface PacienteClientRest {

    @GetMapping("/{id}")
    Paciente detalle(@PathVariable Long id);

    @GetMapping("/usuario/{usuarioId}")
    Paciente detallePorUsuarioId(@PathVariable Long usuarioId);
}
