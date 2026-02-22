package org.nova.ing.springcloud.atencion.medica.msvc.paciente.services;

import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.Cita;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities.PacienteEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.CrearPacienteDto;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto.Diagnostico;

import java.util.List;
import java.util.Optional;

public interface PacienteService {
    List<PacienteEntity> listar();
    Optional<PacienteEntity> porId(Long id);
    PacienteEntity guardar(PacienteEntity paciente);
    PacienteEntity crear(CrearPacienteDto dto);
    void eliminar(Long id);
    void eliminarPermanente(Long id);
    List<Cita> obtenerCitas(Long id);
    @Deprecated
    Cita agendarCita(Cita cita);
    List<Diagnostico> obtenerHistorialMedico(Long id);
    Cita cambiarEstadoCita(Long citaId, Long pacienteId, String nuevoEstado);
    Optional<PacienteEntity> porUsuarioId(Long usuarioId);
}
