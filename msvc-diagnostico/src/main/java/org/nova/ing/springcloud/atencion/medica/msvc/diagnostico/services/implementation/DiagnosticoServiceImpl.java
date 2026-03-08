package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.services.implementation;

import lombok.extern.slf4j.Slf4j; // 1. Importación para el log
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients.CitaClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients.PacienteClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients.SemanticClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.DiagnosticoDetalle;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.DiagnosticoSyncDTO;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.repositories.DiagnosticoRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.services.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DiagnosticoServiceImpl implements DiagnosticoService {

    @Autowired
    private DiagnosticoRepository repository;

    @Autowired
    private CitaClientRest citaClient;

    @Autowired
    private PacienteClientRest pacienteClient;

    @Autowired
    private SemanticClientRest semanticClient;

    @Override
    @Transactional
    public DiagnosticoEntity guardar(DiagnosticoEntity diagnostico) {
        // 1. Guardar en base de datos local (Postgres)
        DiagnosticoEntity guardado = repository.save(diagnostico);

        // 2. Sincronización Semántica
        try {
            DiagnosticoSyncDTO syncDTO = DiagnosticoSyncDTO.builder()
                    .id(guardado.getId())
                    .citaId(guardado.getCitaId())
                    .descripcion(guardado.getDescripcion())
                    .tipoDiagnostico(guardado.getTipoDiagnostico().name())
                    .build();

            semanticClient.sincronizarDiagnostico(syncDTO);

            log.info("Diagnóstico ID {} sincronizado con el Grafo Semántico para la Cita {}",
                    guardado.getId(), guardado.getCitaId());

        } catch (Exception e) {
            log.error("Error al sincronizar diagnóstico con Web Semántica: {}", e.getMessage());
        }

        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosticoEntity> listar() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiagnosticoEntity> porId(Long id) {
        return repository.findById(id);
    }

    // ELIMINADO: Aquí estaba el método guardar() duplicado que causaba el error

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<DiagnosticoEntity> o = repository.findById(id);
        if (o.isPresent()) {
            DiagnosticoEntity diagnostico = o.get();
            diagnostico.setActivo(false);
            repository.save(diagnostico);
        }
    }

    @Override
    @Transactional
    public void eliminarPermanente(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosticoEntity> listarPorCita(Long citaId) {
        return repository.findByCitaId(citaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosticoEntity> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiagnosticoDetalle> porIdConDetalle(Long id) {
        Optional<DiagnosticoEntity> o = repository.findById(id);
        if (o.isPresent()) {
            DiagnosticoEntity diagnostico = o.get();
            DiagnosticoDetalle detalle = new DiagnosticoDetalle();
            detalle.setDiagnostico(diagnostico);
            try {
                detalle.setCita(citaClient.detalle(diagnostico.getCitaId()));
            } catch (Exception e) {
                log.error("Error al obtener detalle de cita: {}", e.getMessage());
            }
            try {
                detalle.setPaciente(pacienteClient.detalle(diagnostico.getPacienteId()));
            } catch (Exception e) {
                log.error("Error al obtener detalle de paciente: {}", e.getMessage());
            }
            return Optional.of(detalle);
        }
        return Optional.empty();
    }
}