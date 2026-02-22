package org.nova.ing.springcloud.atencion.medica.msvc.medico.models.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;

@Getter
@Setter
public class CrearMedicoDto {
    @Valid
    private MedicoEntity medico;
}
