package org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto;

import lombok.Data;

@Data
public class Medico {
    private Long id;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private String telefono;
    private String dni;
    private String email;
    private String estado;
}
