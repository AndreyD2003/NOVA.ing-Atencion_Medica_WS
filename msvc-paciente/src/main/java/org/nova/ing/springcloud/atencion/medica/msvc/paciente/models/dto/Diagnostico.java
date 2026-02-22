package org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto;

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
    private boolean activo;
}
