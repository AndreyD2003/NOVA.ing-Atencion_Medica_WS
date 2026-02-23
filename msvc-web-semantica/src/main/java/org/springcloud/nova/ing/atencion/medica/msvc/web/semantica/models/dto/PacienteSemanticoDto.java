package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PacienteSemanticoDto {

    private Long pacienteId;

    private String iri;

    private String nombres;

    private String apellidos;

    private Date fechaNacimiento;

    private String genero;

    private String dni;

    private String telefono;

    private String email;

    private String direccion;

    private String estado;
}

