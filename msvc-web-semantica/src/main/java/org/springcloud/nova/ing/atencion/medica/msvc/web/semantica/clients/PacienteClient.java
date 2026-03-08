package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients;

import com.github.andrewoma.dexx.collection.Map;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load.PacienteLoadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "msvc-paciente", url = "localhost:8083")
public interface PacienteClient {
    @GetMapping("/pacientes")
    List<PacienteLoadDTO> listarTodos();
}