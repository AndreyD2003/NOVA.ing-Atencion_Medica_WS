package org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto;

import lombok.Data;
import java.util.Date;

@Data
public class Cita {
    private Long id;
    private Date fechaCita;
    private String horaInicio;
    private String horaFin;
    private String motivo;
    private String estado;
    private Long medicoId;
    private Long pacienteId;
}
