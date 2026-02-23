package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote;

import lombok.Data;

import java.util.List;

@Data
public class MedicoRemoteDto {

    private Long id;

    private String nombres;

    private String apellidos;

    private String especialidad;

    private String telefono;

    private String email;

    private String dni;

    private String estado;

    private List<HorarioRemoteDto> horarios;
}
