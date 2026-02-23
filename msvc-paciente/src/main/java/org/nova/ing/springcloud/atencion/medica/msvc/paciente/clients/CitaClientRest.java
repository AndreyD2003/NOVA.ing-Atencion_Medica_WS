package org.nova.ing.springcloud.atencion.medica.msvc.paciente.clients;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.Cita;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "msvc-cita", url = "http://localhost:8081/citas")
public interface CitaClientRest {

    @GetMapping("/paciente/{id}")
    List<Cita> listarPorPaciente(@PathVariable Long id);

    @PostMapping
    Cita crear(@RequestBody Cita cita);

    @GetMapping("/{id}")
    Cita obtenerPorId(@PathVariable Long id);

    @PutMapping("/{id}")
    Cita actualizar(@PathVariable Long id, @RequestBody Cita cita);
}
