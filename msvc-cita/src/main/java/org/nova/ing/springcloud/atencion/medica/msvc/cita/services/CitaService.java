package org.nova.ing.springcloud.atencion.medica.msvc.cita.services;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto.CitaDetalle;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;

import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<CitaEntity> listar();
    Optional<CitaEntity> porId(Long id);
    Optional<CitaDetalle> porIdConDetalle(Long id);
    List<CitaEntity> listarPorPaciente(Long pacienteId);
    List<CitaEntity> listarPorMedico(Long medicoId);
    CitaEntity guardar(CitaEntity cita);
    void eliminar(Long id);
    void eliminarPermanente(Long id);
}
