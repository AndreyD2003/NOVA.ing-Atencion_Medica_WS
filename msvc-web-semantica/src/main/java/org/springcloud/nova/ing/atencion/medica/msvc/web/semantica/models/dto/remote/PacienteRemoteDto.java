package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote;

import lombok.Data;

import java.util.Date;

@Data
public class PacienteRemoteDto {

    private Long id;

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

