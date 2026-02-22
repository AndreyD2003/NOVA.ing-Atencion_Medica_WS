package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.services.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.DiagnosticoDetalle;

@RestController
@RequestMapping("/diagnosticos")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService service;

    @GetMapping
    public List<DiagnosticoEntity> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id, HttpServletRequest request) {

        Optional<DiagnosticoEntity> diagnosticoOptional = service.porId(id);
        if (diagnosticoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DiagnosticoEntity diagnostico = diagnosticoOptional.get();
        return ResponseEntity.ok(diagnostico);
    }

    @GetMapping("/con-detalle/{id}")
    public ResponseEntity<?> detalleCompleto(@PathVariable Long id, HttpServletRequest request) {
        Optional<DiagnosticoDetalle> diagnosticoOptional = service.porIdConDetalle(id);
        if (!diagnosticoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        DiagnosticoDetalle detalle = diagnosticoOptional.get();
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/cita/{id}")
    public ResponseEntity<List<DiagnosticoEntity>> listarPorCita(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorCita(id));
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<?> listarPorPaciente(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(service.listarPorPaciente(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody DiagnosticoEntity diagnostico, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(diagnostico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody DiagnosticoEntity diagnostico, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }
        Optional<DiagnosticoEntity> o = service.porId(id);
        if (o.isPresent()) {
            DiagnosticoEntity diagnosticoDb = o.get();
            diagnosticoDb.setDescripcion(diagnostico.getDescripcion());
            diagnosticoDb.setTipoDiagnostico(diagnostico.getTipoDiagnostico());
            diagnosticoDb.setFechaDiagnostico(diagnostico.getFechaDiagnostico());
            diagnosticoDb.setCitaId(diagnostico.getCitaId());
            diagnosticoDb.setPacienteId(diagnostico.getPacienteId());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(diagnosticoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<DiagnosticoEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> eliminarPermanente(@PathVariable Long id) {
        Optional<DiagnosticoEntity> o = service.porId(id);
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
