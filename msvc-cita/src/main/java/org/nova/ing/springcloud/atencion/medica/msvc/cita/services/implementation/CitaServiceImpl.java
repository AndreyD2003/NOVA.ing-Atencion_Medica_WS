package org.nova.ing.springcloud.atencion.medica.msvc.cita.services.implementation;

import lombok.extern.slf4j.Slf4j;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.clients.*;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.enums.EstadoCita;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.*;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.repositories.CitaRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    @Autowired
    private SemanticClientRest semanticClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
                detalle.setMedico(medicoClient.detalle(cita.getMedicoId()));
                detalle.setDiagnosticos(diagnosticoClient.listarPorCita(cita.getId()));
            } catch (Exception e) {
                log.error("Error al obtener detalles externos: {}", e.getMessage());
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
        if (cita.getFechaCita() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cita.getFechaCita());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            cita.setFechaCita(calendar.getTime());
        }

        // Validaciones de conflicto
        if (repository.existsConflictMedico(cita.getMedicoId(), cita.getFechaCita(), cita.getHoraInicio(), cita.getHoraFin(), cita.getId())) {
            throw new RuntimeException("Conflicto de horario médico.");
        }
        if (repository.existsConflictPaciente(cita.getPacienteId(), cita.getFechaCita(), cita.getHoraInicio(), cita.getHoraFin(), cita.getId())) {
            throw new RuntimeException("Conflicto de horario paciente.");
        }

        // Obtener detalles para la validación y sincronización posterior
        Medico medico = medicoClient.detalle(cita.getMedicoId());
        Paciente paciente = pacienteClient.detalle(cita.getPacienteId());

        if (medico == null || !"ACTIVO".equalsIgnoreCase(medico.getEstado()))
            throw new RuntimeException("Médico no encontrado o inactivo.");
        if (paciente == null || !"ACTIVO".equalsIgnoreCase(paciente.getEstado()))
            throw new RuntimeException("Paciente no encontrado o inactivo.");

        CitaEntity citaGuardada = repository.save(cita);

        // Sincronización Semántica
        try {
            sincronizarConGrafo(citaGuardada, paciente, medico);
        } catch (Exception e) {
            log.error("Error al sincronizar con Grafo Semántico: {}", e.getMessage());
        }

        return citaGuardada;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<CitaEntity> o = repository.findById(id);
        if (o.isPresent()) {
            CitaEntity cita = o.get();
            cita.setEstado(EstadoCita.CANCELADA);
            repository.save(cita);

            try {
                sincronizarConGrafo(cita, pacienteClient.detalle(cita.getPacienteId()), medicoClient.detalle(cita.getMedicoId()));
            } catch (Exception e) {
                log.error("Error al sincronizar cancelación: {}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void eliminarPermanente(Long id) {
        repository.deleteById(id);
    }

    // Método privado para manejar la lógica de sincronización
    private void sincronizarConGrafo(CitaEntity cita, Paciente paciente, Medico medico) {
        SyncRequestDTO syncDTO = SyncRequestDTO.builder()
                .citaId(cita.getId())
                .fechaCita(dateFormat.format(cita.getFechaCita()))
                .horaInicio(cita.getHoraInicio().toString())
                .horaFin(cita.getHoraFin().toString())
                .motivo(cita.getMotivo())
                .estadoCita(cita.getEstado().name())
                .pacienteId(paciente.getId())
                .dniPaciente(paciente.getDni())
                .nombrePaciente(paciente.getNombres())
                .apellidoPaciente(paciente.getApellidos())
                .emailPaciente(paciente.getEmail())
                .medicoId(medico.getId())
                .dniMedico(medico.getDni())
                .nombreMedico(medico.getNombres())
                .especialidad(medico.getEspecialidad())
                .build();

        if (cita.getEstado() == EstadoCita.REALIZADA) {
            try {
                List<Diagnostico> diags = diagnosticoClient.listarPorCita(cita.getId());
                syncDTO.setDiagnosticos(diags.stream()
                        .map(d -> SyncRequestDTO.DiagnosticoSyncDTO.builder()
                                .descripcion(d.getDescripcion())
                                .tipoDiagnostico(d.getTipoDiagnostico())
                                .build())
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                log.warn("No se pudieron adjuntar diagnósticos.");
            }
        }

        // Esta llamada debe coincidir con el nombre en SemanticClientRest
        semanticClient.sincronizar(syncDTO);
    }
}