package org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto;

import lombok.Data;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;

import java.util.List;

@Data
public class CitaDetalle {
    private CitaEntity cita;
    private Paciente paciente;
    private Medico medico;
    private List<Diagnostico> diagnosticos;
}
