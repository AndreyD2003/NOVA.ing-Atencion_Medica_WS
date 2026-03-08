package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequestDTO {

    // --- Identificadores de Base de Datos ---
    private Long citaId;
    private Long pacienteId;
    private Long medicoId;

    // --- Datos de la Cita (msvc-cita) ---
    private String fechaCita;   // Formato esperado: yyyy-MM-dd
    private String horaInicio;
    private String horaFin;
    private String motivo;
    private String estadoCita;  // Ej: PROGRAMADA, REALIZADA, CANCELADA

    // --- Datos del Paciente (msvc-paciente) ---
    private String dniPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String generoPaciente;
    private String emailPaciente;

    // --- Datos del Médico (msvc-medico) ---
    private String dniMedico;
    private String nombreMedico;
    private String apellidoMedico;
    private String especialidad; // Ej: CARDIOLOGIA, PEDIATRIA, etc.

    // --- Diagnósticos Relacionados (msvc-diagnostico) ---
    // Usamos DiagnosticoSyncDTO para evitar crear clases duplicadas
    private List<DiagnosticoSyncDTO> diagnosticos;
}