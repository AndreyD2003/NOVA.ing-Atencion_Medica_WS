package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load;

import lombok.Data;

@Data
public class PacienteLoadDTO {
    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String email;
}