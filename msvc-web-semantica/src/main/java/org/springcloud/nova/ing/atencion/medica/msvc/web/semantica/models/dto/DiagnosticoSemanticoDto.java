package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DiagnosticoSemanticoDto {

    private Long diagnosticoId;

    private String iri;

    private String descripcion;

    private String tipoDiagnostico;

    private Date fechaDiagnostico;

    private Long citaId;

    private Long pacienteId;

    private String citaIri;

    private String pacienteIri;
}

