package org.nova.ing.springcloud.atencion.medica.msvc.medico.clients;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.Cita;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "msvc-cita", url = "http://localhost:8081/citas")
public interface CitaClientRest {

    @GetMapping("/medico/{id}")
    List<Cita> listarPorMedico(@PathVariable Long id);

    @GetMapping("/{id}")
    Cita detalle(@PathVariable Long id);

    @PostMapping
    Cita crear(@RequestBody Cita cita);
}
