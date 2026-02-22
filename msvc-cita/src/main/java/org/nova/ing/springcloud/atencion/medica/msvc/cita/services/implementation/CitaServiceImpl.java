package org.nova.ing.springcloud.atencion.medica.msvc.cita.services.implementation;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.clients.DiagnosticoClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.clients.MedicoClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.clients.PacienteClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.enums.EstadoCita;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.CitaDetalle;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.Medico;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.Paciente;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.repositories.CitaRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository repository;

    @Autowired
    private PacienteClientRest pacienteClient;

    @Autowired
    private MedicoClientRest medicoClient;

    @Autowired
    private DiagnosticoClientRest diagnosticoClient;

    @Override
    @Transactional(readOnly = true)
    public List<CitaEntity> listar() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CitaEntity> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CitaDetalle> porIdConDetalle(Long id) {
        Optional<CitaEntity> o = repository.findById(id);
        if (o.isPresent()) {
            CitaEntity cita = o.get();
            CitaDetalle detalle = new CitaDetalle();
            detalle.setCita(cita);
            try {
                detalle.setPaciente(pacienteClient.detalle(cita.getPacienteId()));
            } catch (Exception e) {
                // TODO: Handle exception (log it)
            }
            try {
                detalle.setMedico(medicoClient.detalle(cita.getMedicoId()));
            } catch (Exception e) {
                // TODO: Handle exception
            }
            try {
                detalle.setDiagnosticos(diagnosticoClient.listarPorCita(cita.getId()));
            } catch (Exception e) {
                // Handle exception
            }
            return Optional.of(detalle);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaEntity> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaEntity> listarPorMedico(Long medicoId) {
        return repository.findByMedicoId(medicoId);
    }

    @Override
    @Transactional
    public CitaEntity guardar(CitaEntity cita) {
        // Normalizar fecha (remover componente de tiempo)
        if (cita.getFechaCita() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cita.getFechaCita());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            cita.setFechaCita(calendar.getTime());
        }

        // Validar conflictos de horario
        if (repository.existsConflictMedico(cita.getMedicoId(), cita.getFechaCita(), cita.getHoraInicio(), cita.getHoraFin(), cita.getId())) {
            throw new RuntimeException("Conflicto de horario: El médico ya tiene una cita programada en este intervalo.");
        }

        if (repository.existsConflictPaciente(cita.getPacienteId(), cita.getFechaCita(), cita.getHoraInicio(), cita.getHoraFin(), cita.getId())) {
            throw new RuntimeException("Conflicto de horario: El paciente ya tiene una cita programada en este intervalo.");
        }

        Medico medico;
        try {
            medico = medicoClient.detalle(cita.getMedicoId());
        } catch (Exception e) {
            throw new RuntimeException("Medico no encontrado con ID: " + cita.getMedicoId());
        }

        if (medico == null || !"ACTIVO".equalsIgnoreCase(medico.getEstado())) {
            throw new RuntimeException("El médico con ID " + cita.getMedicoId() + " no se encuentra activo para realizar consultas.");
        }

        Paciente paciente;
        try {
            paciente = pacienteClient.detalle(cita.getPacienteId());
        } catch (Exception e) {
            throw new RuntimeException("Paciente no encontrado con ID: " + cita.getPacienteId());
        }

        if (paciente == null || !"ACTIVO".equalsIgnoreCase(paciente.getEstado())) {
            throw new RuntimeException("El paciente con ID " + cita.getPacienteId() + " no se encuentra activo.");
        }

        return repository.save(cita);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<CitaEntity> o = repository.findById(id);
        if (o.isPresent()) {
            CitaEntity cita = o.get();
            cita.setEstado(EstadoCita.CANCELADA);
            repository.save(cita);
        }
    }

    @Override
    @Transactional
    public void eliminarPermanente(Long id) {
        repository.deleteById(id);
    }
}
