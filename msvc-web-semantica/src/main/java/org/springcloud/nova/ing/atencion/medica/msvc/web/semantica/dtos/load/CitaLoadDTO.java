package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load;

import lombok.Data;

@Data
public class CitaLoadDTO {
    private Long id;
    private String fechaCita;
    private String horaInicio;
    private String horaFin;
    private String motivo;
    private String estado;
    private Long pacienteId;
    private Long medicoId;
}