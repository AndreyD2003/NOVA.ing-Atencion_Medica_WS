package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CitaSemanticaDto {

    private Long citaId;

    private String iri;

    private Date fechaCita;

    private String horaInicio;

    private String horaFin;

    private String motivo;

    private String estado;

    private Long pacienteId;

    private Long medicoId;

    private String pacienteIri;

    private String medicoIri;
}

