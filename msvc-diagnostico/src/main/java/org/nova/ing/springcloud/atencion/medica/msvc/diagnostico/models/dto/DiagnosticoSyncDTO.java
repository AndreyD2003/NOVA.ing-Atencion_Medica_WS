package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiagnosticoSyncDTO {
    private Long id;
    private Long citaId;
    private String descripcion;
    private String tipoDiagnostico;
}