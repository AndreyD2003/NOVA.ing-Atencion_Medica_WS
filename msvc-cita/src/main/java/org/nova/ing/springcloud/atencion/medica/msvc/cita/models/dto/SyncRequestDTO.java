package org.nova.ing.springcloud.atencion.medica.msvc.cita.models.dto;

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

    // --- Datos Identificadores (IDs de base de datos) ---
    private Long citaId;
    private Long pacienteId;
    private Long medicoId;

    // --- Datos de la Cita (msvc-cita) ---
    private String fechaCita;    // Formato: yyyy-MM-dd
    private String horaInicio;   // Formato: HH:mm:ss
    private String horaFin;
    private String motivo;
    private String estadoCita;   // PROGRAMADA, CANCELADA, REALIZADA, REPROGRAMADA

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
    private String especialidad;  // CARDIOLOGIA, PEDIATRIA, etc.

    // --- Diagnósticos (Opcional, msvc-diagnostico) ---
    // Se usa cuando se sincroniza una cita que ya fue REALIZADA
    private List<DiagnosticoSyncDTO> diagnosticos;

    /**
     * Clase interna para agrupar diagnósticos en la sincronización
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosticoSyncDTO {
        private String descripcion;
        private String tipoDiagnostico; // PRESUNTIVO, DEFINITIVO, etc.
    }
}