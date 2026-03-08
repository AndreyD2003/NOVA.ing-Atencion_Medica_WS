import { api } from '@/api/axios';
import type { SyncRequestDTO, SparqlResult, SemanticSuggestion } from '@/types/semantic.types';

/**
 * Servicio alineado con los endpoints reales del SemanticController (msvc-web-semantica).
 *
 * Backend endpoints:
 *   GET  /api/v1/semantic/buscar?texto=...   → Búsqueda en lenguaje natural
 *   POST /api/v1/semantic/sync               → Sincronizar una atención médica
 *   POST /api/v1/semantic/bulk-load           → Carga masiva desde todos los microservicios
 *   POST /api/v1/semantic/sparql              → Consulta SPARQL directa
 */
export const semanticService = {
  /**
   * Búsqueda en lenguaje natural.
   * El backend parsea el texto buscando: DNI (8 dígitos), especialidad, estado, fecha (yyyy-MM-dd).
   * GET /api/v1/semantic/buscar?texto=...
   */
  buscar: async (texto: string): Promise<SparqlResult[]> => {
    const { data } = await api.get<SparqlResult[]>(
      `/api/v1/semantic/buscar`,
      { params: { texto } }
    );
    return data;
  },

  /**
   * Sincronizar datos de una atención médica completa en el grafo semántico.
   * POST /api/v1/semantic/sync
   */
  sync: async (dto: SyncRequestDTO): Promise<string> => {
    const { data } = await api.post<string>('/api/v1/semantic/sync', dto);
    return data;
  },

  /**
   * Carga masiva: sincroniza TODOS los datos (pacientes, médicos, citas, diagnósticos)
   * desde los otros microservicios hacia el grafo semántico de Fuseki.
   * POST /api/v1/semantic/bulk-load
   */
  bulkLoad: async (): Promise<string> => {
    const { data } = await api.post<string>('/api/v1/semantic/bulk-load');
    return data;
  },

  /**
   * Ejecutar consulta SPARQL directa (administración / debug).
   * POST /api/v1/semantic/sparql  —  Body: { "query": "..." }
   */
  sparql: async (query: string): Promise<SparqlResult[]> => {
    const { data } = await api.post<SparqlResult[]>('/api/v1/semantic/sparql', { query });
    return data;
  },
};

/**
 * Sugerencias estáticas basadas en las capacidades del QueryParser del backend.
 * Soporta: DNI, especialidades, estados, fechas relativas, rangos, ranking, disponibilidad, nombres y diagnósticos.
 */
export const SEMANTIC_SUGGESTIONS: SemanticSuggestion[] = [
  // ── Por Especialidad ──
  {
    titulo: 'Citas de Cardiología',
    query: 'citas de cardiologia',
    descripcion: 'Todas las citas con especialidad Cardiología',
  },
  {
    titulo: 'Citas de Pediatría',
    query: 'citas de pediatria',
    descripcion: 'Todas las citas con especialidad Pediatría',
  },
  {
    titulo: 'Citas de Medicina General',
    query: 'citas de medicina general',
    descripcion: 'Todas las citas de Medicina General',
  },
  // ── Por Estado ──
  {
    titulo: 'Citas Programadas',
    query: 'citas programadas',
    descripcion: 'Citas con estado PROGRAMADA',
  },
  {
    titulo: 'Citas Realizadas',
    query: 'citas realizadas',
    descripcion: 'Citas con estado REALIZADA',
  },
  {
    titulo: 'Citas Canceladas',
    query: 'citas canceladas',
    descripcion: 'Citas con estado CANCELADA',
  },
  // ── Por Fecha ──
  {
    titulo: 'Citas de Hoy',
    query: 'citas de hoy',
    descripcion: 'Todas las citas del día de hoy',
  },
  {
    titulo: 'Citas de esta Semana',
    query: 'citas esta semana',
    descripcion: 'Todas las citas de la semana actual (lunes a domingo)',
  },
  {
    titulo: 'Citas de este Mes',
    query: 'citas este mes',
    descripcion: 'Todas las citas del mes actual',
  },
  {
    titulo: 'Citas entre fechas',
    query: 'Citas del 2026-02-26 al 2026-03-29',
    descripcion: 'Busca citas en un rango de fechas específico',
  },
  // ── Combinadas ──
  {
    titulo: 'Cardiología Programadas Hoy',
    query: 'citas de cardiologia programadas hoy',
    descripcion: 'Citas de cardiología con estado programada para hoy',
  },
  // ── Rankings ──
  {
    titulo: 'Top 5 Médicos',
    query: 'top 5 medicos con mas citas',
    descripcion: 'Ranking de médicos con más citas atendidas',
  },
  {
    titulo: 'Top 3 Médicos (menos citas)',
    query: 'top 3 medicos con menos citas',
    descripcion: 'Médicos con menos citas (orden ascendente)',
  },
  // ── Por DNI ──
  {
    titulo: 'Buscar por DNI',
    query: '70000001',
    descripcion: 'Busca por DNI de paciente o médico (edita con el DNI real)',
  },
  // ── Disponibilidad ──
  {
    titulo: 'Disponibilidad Médica',
    query: 'médicos disponibles hoy 10:00',
    descripcion: 'Médicos libres hoy a las 10:00 (edita fecha y hora)',
  },
];

