package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.services;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto.DiagnosticoDetalle;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;

import java.util.List;
import java.util.Optional;

public interface DiagnosticoService {
    List<DiagnosticoEntity> listar();
    Optional<DiagnosticoEntity> porId(Long id);
    DiagnosticoEntity guardar(DiagnosticoEntity diagnostico);
    void eliminar(Long id);
    void eliminarPermanente(Long id);
    List<DiagnosticoEntity> listarPorCita(Long citaId);
    List<DiagnosticoEntity> listarPorPaciente(Long pacienteId);
    Optional<DiagnosticoDetalle> porIdConDetalle(Long id);
}
