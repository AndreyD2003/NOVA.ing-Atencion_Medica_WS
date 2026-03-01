import { api } from '@/api/axios';

export interface Diagnosis {
  id?: number;
  descripcion: string;
  tipoDiagnostico: string;
  fechaDiagnostico: string;
  citaId: number;
  pacienteId: number;
}

export const diagnosisService = {
  getAll: async () => {
    const { data } = await api.get<Diagnosis[]>('/api/diagnosticos');
    return data;
  },
  getById: async (id: number) => {
    const { data } = await api.get<Diagnosis>(`/api/diagnosticos/${id}`);
    return data;
  },
  getWithDetails: async (id: number) => {
      const { data } = await api.get(`/api/diagnosticos/con-detalle/${id}`);
      return data;
  },
  create: async (diagnosis: Diagnosis) => {
    const { data } = await api.post<Diagnosis>('/api/diagnosticos', diagnosis);
    return data;
  },
  update: async (id: number, diagnosis: Diagnosis) => {
    const { data } = await api.put<Diagnosis>(`/api/diagnosticos/${id}`, diagnosis);
    return data;
  },
  delete: async (id: number) => {
    await api.delete(`/api/diagnosticos/${id}`);
  },
  deleteForce: async (id: number) => {
    await api.delete(`/api/diagnosticos/${id}/force`);
  },
  getByPatient: async (id: number) => {
    const { data } = await api.get(`/api/diagnosticos/paciente/${id}`);
    return data;
  },
  getByAppointment: async (id: number) => {
      const { data } = await api.get(`/api/diagnosticos/cita/${id}`);
      return data;
  }
};
