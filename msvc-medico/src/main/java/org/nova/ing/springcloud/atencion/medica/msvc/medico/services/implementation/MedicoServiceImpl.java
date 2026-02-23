package org.nova.ing.springcloud.atencion.medica.msvc.medico.services.implementation;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.clients.CitaClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.clients.DiagnosticoClientRest;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EstadoMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.*;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.repositories.MedicoRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MedicoServiceImpl implements MedicoService {

    @Autowired
    private MedicoRepository repository;

    @Autowired
    private CitaClientRest citaClient;

    @Autowired
    private DiagnosticoClientRest diagnosticoClient;
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicoEntity> listar() {
        return repository.findAllWithHorarios();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicoEntity> porId(Long id) {
        return repository.findByIdWithHorarios(id);
    }

    @Override
    @Transactional
    public MedicoEntity guardar(MedicoEntity medico) {
        // Handle bidirectional relationship logic if needed
        if (medico.getHorarios() != null) {
            medico.getHorarios().forEach(h -> h.setMedico(medico));
        }
        return repository.save(medico);
    }

    @Override
    @Transactional
    public MedicoEntity crear(CrearMedicoDto dto) {
        MedicoEntity medico = dto.getMedico();
        return guardar(medico);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<MedicoEntity> o = repository.findById(id);
        if (o.isPresent()) {
            MedicoEntity medico = o.get();
            medico.setEstado(EstadoMedico.INACTIVO);
            repository.save(medico);
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
    public Diagnostico registrarDiagnostico(Diagnostico diagnostico) {
        // Validar que la cita exista
        Cita cita = citaClient.detalle(diagnostico.getCitaId());
        if (cita == null) {
            throw new RuntimeException("La cita con ID " + diagnostico.getCitaId() + " no existe.");
        }
        return diagnosticoClient.crear(diagnostico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerCitas(Long id) {
        try {
            return citaClient.listarPorMedico(id);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicoEntity> porUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

}
