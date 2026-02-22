package org.nova.ing.springcloud.atencion.medica.msvc.paciente.services.implementations;

import org.nova.ing.springcloud.atencion.medica.msvc.paciente.clients.CitaClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.clients.DiagnosticoClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.enums.EstadoPaciente;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.*;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities.PacienteEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.repositories.PacienteRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Autowired
    private CitaClientRest citaClient;

    @Autowired
    private DiagnosticoClientRest diagnosticoClient;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteEntity> listar() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PacienteEntity> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public PacienteEntity guardar(PacienteEntity paciente) {
        return repository.save(paciente);
    }

    @Override
    @Transactional
    public PacienteEntity crear(CrearPacienteDto dto) {
        PacienteEntity paciente = dto.getPaciente();
        return repository.save(paciente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<PacienteEntity> o = repository.findById(id);
        if (o.isPresent()) {
            PacienteEntity paciente = o.get();
            paciente.setEstado(EstadoPaciente.INACTIVO);
            repository.save(paciente);
        }
    }

    @Override
    @Transactional
    public void eliminarPermanente(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Cita agendarCita(Cita cita) {
        return citaClient.crear(cita);
    }

    @Override
    public List<Diagnostico> obtenerHistorialMedico(Long id) {
        return diagnosticoClient.listarPorPaciente(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerCitas(Long id) {
        try {
            return citaClient.listarPorPaciente(id);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Cita cambiarEstadoCita(Long citaId, Long pacienteId, String nuevoEstado) {
        // 1. Obtener la cita del servicio de citas
        Cita cita = citaClient.obtenerPorId(citaId);
        if (cita == null) {
            throw new IllegalArgumentException("Cita not found");
        }

        // 2. Verificar que el paciente existe
        Optional<PacienteEntity> pacienteOptional = repository.findById(pacienteId);
        if (pacienteOptional.isEmpty()) {
            throw new IllegalArgumentException("Paciente not found");
        }
        PacienteEntity paciente = pacienteOptional.get();

        // 3. Verificar que el pacienteId coincide con el asignado en la cita
        if (!pacienteId.equals(cita.getPacienteId())) {
            throw new SecurityException("Patient is not assigned to this appointment");
        }

        // 4. Cambiar estado y actualizar en servicio de citas
        cita.setEstado(nuevoEstado);
        return citaClient.actualizar(citaId, cita);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PacienteEntity> porUsuarioId(Long usuarioId) {
        // Asegúrate de que 'repository' sea el nombre de tu PacienteRepository inyectado
        return repository.findByUsuarioId(usuarioId);
    }
}
