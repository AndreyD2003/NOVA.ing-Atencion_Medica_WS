package org.nova.ing.springcloud.atencion.medica.msvc.cita.clients;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.Medico;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-medico", url = "http://localhost:8080/medicos")
public interface MedicoClientRest {

    @GetMapping("/{id}")
    Medico detalle(@PathVariable Long id);

    @GetMapping("/usuario/{usuarioId}")
    Medico detallePorUsuarioId(@PathVariable Long usuarioId);
}
