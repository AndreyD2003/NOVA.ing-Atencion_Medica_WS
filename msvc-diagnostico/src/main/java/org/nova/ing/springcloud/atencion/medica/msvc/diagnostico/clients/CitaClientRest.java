package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.Cita;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-cita", url = "http://localhost:8081/citas")
public interface CitaClientRest {

    @GetMapping("/{id}")
    Cita detalle(@PathVariable Long id);
}
