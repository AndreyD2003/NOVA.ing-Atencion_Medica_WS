package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos;

import lombok.Data;

@Data
public class DiagnosticoSyncDTO {
    private Long id;
    private Long citaId;
    private String descripcion;
    private String tipoDiagnostico;
}