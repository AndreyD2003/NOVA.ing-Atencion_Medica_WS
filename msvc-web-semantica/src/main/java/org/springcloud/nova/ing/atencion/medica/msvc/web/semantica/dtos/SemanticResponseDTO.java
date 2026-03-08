package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SemanticResponseDTO {
    private String citaUri;
    private String fecha;
    private String estado;
    private String pacienteNombre;
    private String pacienteDni;
    private String medicoNombre;
    private String especialidad;
    private String motivo;
}