package org.nova.ing.springcloud.atencion.medica.msvc.medico.controllers;

import jakarta.validation.Valid;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.CrearMedicoDto;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.Cita;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.Diagnostico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService service;

    @GetMapping
    public List<MedicoEntity> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<MedicoEntity> medicoOptional = service.porId(id);
        if (medicoOptional.isPresent()) {
            return ResponseEntity.ok(medicoOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/citas")
    public ResponseEntity<?> obtenerCitas(@PathVariable Long id) {
        Optional<MedicoEntity> o = service.porId(id);
        if (o.isPresent()) {
            return ResponseEntity.ok(service.obtenerCitas(id));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearMedicoDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody MedicoEntity medico, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }
        Optional<MedicoEntity> o = service.porId(id);
        if (o.isPresent()) {
            MedicoEntity medicoDb = o.get();
            medicoDb.setNombres(medico.getNombres());
            medicoDb.setApellidos(medico.getApellidos());
            medicoDb.setEmail(medico.getEmail());
            medicoDb.setDni(medico.getDni());
            medicoDb.setTelefono(medico.getTelefono());
            medicoDb.setEspecialidad(medico.getEspecialidad());
            medicoDb.setEstado(medico.getEstado());
            // Update logic for Horarios could be complex, keeping it simple for now
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(medicoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<MedicoEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> eliminarPermanente(@PathVariable Long id) {
        Optional<MedicoEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminarPermanente(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/agendar-cita")
    public ResponseEntity<?> agendarCita(@RequestBody Cita cita) {
        Cita citaCreada = service.agendarCita(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(citaCreada);
    }

    @PostMapping("/registrar-diagnostico")
    public ResponseEntity<?> registrarDiagnostico(@RequestBody Diagnostico diagnostico) {
        Diagnostico diagnosticoCreado = service.registrarDiagnostico(diagnostico);
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticoCreado);
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
