package org.nova.ing.springcloud.atencion.medica.msvc.cita.controllers;

import jakarta.validation.Valid;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.CitaDetalle;

@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService service;

    @GetMapping
    public List<CitaEntity> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<CitaEntity> citaOptional = service.porId(id);
        if (citaOptional.isPresent()) {
            return ResponseEntity.ok(citaOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/con-detalle/{id}")
    public ResponseEntity<?> detalleCompleto(@PathVariable Long id) {
        Optional<CitaDetalle> citaOptional = service.porIdConDetalle(id);
        if (citaOptional.isPresent()) {
            return ResponseEntity.ok(citaOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<CitaEntity>> listarPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorPaciente(id));
    }

    @GetMapping("/medico/{id}")
    public ResponseEntity<List<CitaEntity>> listarPorMedico(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorMedico(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CitaEntity cita, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cita));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody CitaEntity cita, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }
        Optional<CitaEntity> o = service.porId(id);
        if (o.isPresent()) {
            CitaEntity citaDb = o.get();
            citaDb.setFechaCita(cita.getFechaCita());
            citaDb.setHoraInicio(cita.getHoraInicio());
            citaDb.setHoraFin(cita.getHoraFin());
            citaDb.setMotivo(cita.getMotivo());
            citaDb.setEstado(cita.getEstado());
            citaDb.setPacienteId(cita.getPacienteId());
            citaDb.setMedicoId(cita.getMedicoId());
            try {
                return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(citaDb));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<CitaEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> eliminarPermanente(@PathVariable Long id) {
        Optional<CitaEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminarPermanente(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
