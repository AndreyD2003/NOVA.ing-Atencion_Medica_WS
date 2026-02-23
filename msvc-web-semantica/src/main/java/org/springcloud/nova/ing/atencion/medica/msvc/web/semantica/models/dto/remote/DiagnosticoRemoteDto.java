package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote;

import lombok.Data;

import java.util.Date;

@Data
public class DiagnosticoRemoteDto {

    private Long id;

    private String descripcion;

    private String tipoDiagnostico;

    private Date fechaDiagnostico;

    private Long citaId;

    private Long pacienteId;
}

