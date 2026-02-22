package org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto;

import lombok.Data;
import java.util.Date;

@Data
public class Diagnostico {
    private Long id;
    private String descripcion;
    private String tipoDiagnostico;
    private Date fechaDiagnostico;
    private Long citaId;
    private Long pacienteId;
}
