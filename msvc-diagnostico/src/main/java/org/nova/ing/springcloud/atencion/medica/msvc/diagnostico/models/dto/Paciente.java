package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto;

import lombok.Data;
import java.util.Date;

@Data
public class Paciente {
    private Long id;
    private String nombres;
    private String apellidos;
    private Date fechaNacimiento;
    private String genero;
    private String dni;
    private String telefono;
    private String email;
    private String direccion;
}
