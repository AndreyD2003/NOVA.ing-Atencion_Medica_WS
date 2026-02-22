package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto;

import lombok.Data;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;

@Data
public class DiagnosticoDetalle {
    private DiagnosticoEntity diagnostico;
    private Cita cita;
    private Paciente paciente;
}
