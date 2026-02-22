package org.nova.ing.springcloud.atencion.medica.msvc.paciente.controllers;

import jakarta.validation.Valid;

import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.Cita;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.CrearPacienteDto;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.Diagnostico;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities.PacienteEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.services.PacienteService;
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
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    @GetMapping
    public List<PacienteEntity> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<PacienteEntity> pacienteOptional = service.porId(id);
        if (pacienteOptional.isPresent()) {
            return ResponseEntity.ok(pacienteOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/citas")
    public ResponseEntity<?> obtenerCitas(@PathVariable Long id) {
        Optional<PacienteEntity> o = service.porId(id);
        if (o.isPresent()) {
            return ResponseEntity.ok(service.obtenerCitas(id));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearPacienteDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody PacienteEntity paciente, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }
        Optional<PacienteEntity> o = service.porId(id);
        if (o.isPresent()) {
            PacienteEntity pacienteDb = o.get();
            pacienteDb.setNombres(paciente.getNombres());
            pacienteDb.setApellidos(paciente.getApellidos());
            pacienteDb.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteDb.setGenero(paciente.getGenero());
            pacienteDb.setDni(paciente.getDni());
            pacienteDb.setTelefono(paciente.getTelefono());
            pacienteDb.setEmail(paciente.getEmail());
            pacienteDb.setDireccion(paciente.getDireccion());
            pacienteDb.setEstado(paciente.getEstado());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(pacienteDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<PacienteEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<?> eliminarPermanente(@PathVariable Long id) {
        Optional<PacienteEntity> o = service.porId(id);
        if (o.isPresent()) {
            service.eliminarPermanente(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Agendar cita desde modulo paciente
    @PostMapping("/agendar-cita")
    public ResponseEntity<?> agendarCita(@Valid @RequestBody Cita cita, BindingResult result) {
        if (result.hasErrors()) {
            return validar(result);
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.agendarCita(cita));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // Nuevo endpoint: cambiar estado de cita por paciente propietario
    @PatchMapping("/{pacienteId}/citas/{citaId}/estado")
    public ResponseEntity<?> cambiarEstadoCita(@PathVariable Long pacienteId, @PathVariable Long citaId, @RequestBody Map<String, String> payload) {
        String nuevoEstado = payload.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Se requiere campo 'estado'"));
        }
        try {
            Cita citaActualizada = service.cambiarEstadoCita(citaId, pacienteId, nuevoEstado);
            return ResponseEntity.ok(citaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/historial-medico")
    public ResponseEntity<List<Diagnostico>> verHistorialMedico(@PathVariable Long id) {
        List<Diagnostico> historial = service.obtenerHistorialMedico(id);
        return ResponseEntity.ok(historial);
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
