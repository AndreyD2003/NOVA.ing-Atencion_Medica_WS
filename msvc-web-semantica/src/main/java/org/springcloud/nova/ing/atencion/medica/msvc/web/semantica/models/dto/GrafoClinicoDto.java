package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class GrafoClinicoDto {

    private PacienteSemanticoDto paciente;

    private MedicoSemanticoDto medico;

    private CitaSemanticaDto cita;

    private List<DiagnosticoSemanticoDto> diagnosticos;
}

