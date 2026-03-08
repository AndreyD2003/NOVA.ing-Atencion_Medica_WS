package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load;

import lombok.Data;

@Data
public class MedicoLoadDTO {
    private Long id;
    private String dni;
    private String nombres;
    private String especialidad;
}
