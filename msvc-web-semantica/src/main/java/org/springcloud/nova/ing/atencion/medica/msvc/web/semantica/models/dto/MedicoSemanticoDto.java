package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto;

import lombok.Data;

@Data
public class MedicoSemanticoDto {

    private Long medicoId;

    private String iri;

    private String nombres;

    private String apellidos;

    private String especialidad;

    private String telefono;

    private String email;

    private String dni;

    private String estado;
}

