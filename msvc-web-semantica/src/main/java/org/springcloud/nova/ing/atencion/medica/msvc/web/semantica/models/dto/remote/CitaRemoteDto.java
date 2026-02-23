package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote;

import lombok.Data;

import java.util.Date;

@Data
public class CitaRemoteDto {

    private Long id;

    private Date fechaCita;

    private String horaInicio;

    private String horaFin;

    private String motivo;

    private String estado;

    private Long pacienteId;

    private Long medicoId;
}

