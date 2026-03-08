/**
 * Tipos alineados con los DTOs del msvc-web-semantica
 */

export interface DiagnosticoDTO {
  descripcion: string;
  tipoDiagnostico: string; // PRESUNTIVO | DEFINITIVO
}

export interface SyncRequestDTO {
  // Identificadores de Base de Datos
  citaId: number;
  pacienteId: number;
  medicoId: number;

  // Datos de la Cita (msvc-cita)
  fechaCita: string;   // yyyy-MM-dd
  horaInicio: string;
  horaFin: string;
  motivo: string;
  estadoCita: string;  // PROGRAMADA | REALIZADA | CANCELADA

  // Datos del Paciente (msvc-paciente)
  dniPaciente: string;
  nombrePaciente: string;
  apellidoPaciente: string;
  generoPaciente: string;
  emailPaciente: string;

  // Datos del Médico (msvc-medico)
  dniMedico: string;
  nombreMedico: string;
  apellidoMedico: string;
  especialidad: string; // CARDIOLOGIA | PEDIATRIA | DERMATOLOGIA | GINECOLOGIA | MEDICINA_GENERAL

  // Diagnósticos Relacionados (msvc-diagnostico)
  diagnosticos: DiagnosticoDTO[];
}

export interface SemanticResponseDTO {
  citaUri: string;
  fecha: string;
  estado: string;
  pacienteNombre: string;
  pacienteDni: string;
  medicoNombre: string;
  especialidad: string;
  motivo: string;
}

/** Resultado genérico de una consulta SPARQL (clave-valor dinámico) */
export type SparqlResult = Record<string, string>;

/** Sugerencia estática para la UI de búsqueda semántica */
export interface SemanticSuggestion {
  titulo: string;
  query: string;
  descripcion: string;
}

