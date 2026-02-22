package org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities.PacienteEntity;

@Getter
@Setter
public class CrearPacienteDto {
    @Valid
    private PacienteEntity paciente;
}
