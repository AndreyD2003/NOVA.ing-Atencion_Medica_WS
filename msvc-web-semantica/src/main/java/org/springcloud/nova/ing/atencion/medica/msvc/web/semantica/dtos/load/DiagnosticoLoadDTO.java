package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoLoadDTO {
    private Long id;
    private Long citaId;
    private Long pacienteId;
    private String descripcion;
    private String tipoDiagnostico; // Se recibe como String del Enum (PRESUNTIVO, DEFINITIVO, etc.)
    private String fechaDiagnostico;
    private boolean activo;
}