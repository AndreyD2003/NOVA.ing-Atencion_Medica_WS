package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.services.implementation;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients.CitaClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.clients.PacienteClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.DiagnosticoDetalle;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.repositories.DiagnosticoRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.services.DiagnosticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DiagnosticoServiceImpl implements DiagnosticoService {

    @Autowired
    private DiagnosticoRepository repository;

    @Autowired
    private CitaClientRest citaClient;

    @Autowired
    private PacienteClientRest pacienteClient;

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

    @Override
    @Transactional
    public DiagnosticoEntity guardar(DiagnosticoEntity diagnostico) {
        return repository.save(diagnostico);
    }

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
                // Handle exception
            }
            try {
                detalle.setPaciente(pacienteClient.detalle(diagnostico.getPacienteId()));
            } catch (Exception e) {
                // Handle exception
            }
            return Optional.of(detalle);
        }
        return Optional.empty();
    }
}
