package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.CitaDetalleRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.CitaRemoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "msvc-cita", url = "http://localhost:8081/citas")
public interface CitaClientRest {

    @GetMapping
    List<CitaRemoteDto> listarTodas();

    @GetMapping("/con-detalle/{id}")
    CitaDetalleRemoteDto obtenerCitaConDetalle(@PathVariable Long id);
}
