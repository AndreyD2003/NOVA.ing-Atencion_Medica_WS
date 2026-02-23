package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote;

import lombok.Data;

import java.util.List;

@Data
public class CitaDetalleRemoteDto {

    private CitaRemoteDto cita;

    private PacienteRemoteDto paciente;

    private MedicoRemoteDto medico;

    private List<DiagnosticoRemoteDto> diagnosticos;
}

