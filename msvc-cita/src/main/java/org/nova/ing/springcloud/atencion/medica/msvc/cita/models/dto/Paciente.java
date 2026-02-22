package org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto;

import lombok.Data;
import java.util.Date;

@Data
public class Paciente {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
    private Date fechaNacimiento;
    private String direccion;
    private String estado;
}
