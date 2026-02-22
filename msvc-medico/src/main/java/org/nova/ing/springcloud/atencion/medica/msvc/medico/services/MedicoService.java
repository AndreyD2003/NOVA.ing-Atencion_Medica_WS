package org.nova.ing.springcloud.atencion.medica.msvc.medico.services;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.Diagnostico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.Cita;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto.CrearMedicoDto;

import java.util.List;
import java.util.Optional;

public interface MedicoService {
    List<MedicoEntity> listar();
    Optional<MedicoEntity> porId(Long id);
    MedicoEntity guardar(MedicoEntity medico);
    MedicoEntity crear(CrearMedicoDto dto);
    void eliminar(Long id);
    void eliminarPermanente(Long id);
    List<Cita> obtenerCitas(Long id);
    Cita agendarCita(Cita cita);
    Diagnostico registrarDiagnostico(Diagnostico diagnostico);
    Optional<MedicoEntity> porUsuarioId(Long usuarioId);
}
