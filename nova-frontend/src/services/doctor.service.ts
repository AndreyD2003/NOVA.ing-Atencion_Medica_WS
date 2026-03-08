import { api } from '@/api/axios';

export interface DoctorSchedule {
  id?: number;
  diaSemana: string;
  horaInicio: string;
  horaFin: string;
}

export interface Doctor {
  id?: number;
  dni: string;
  nombres: string;
  apellidos: string;
  email: string;
  especialidad: string;
  estado: string;
  telefono: string;
  usuarioId?: number;
  horarios?: DoctorSchedule[];
}

export const doctorService = {
  getAll: async () => {
    const { data } = await api.get<Doctor[]>('/api/medicos');
    return data;
  },
  getById: async (id: number) => {
    const { data } = await api.get<Doctor>(`/api/medicos/${id}`);
    return data;
  },
  create: async (doctor: Doctor) => {
    // Backend espera { "medico": { ...datos } } debido a CrearMedicoDto
    const payload = { medico: doctor };
    const { data } = await api.post<Doctor>('/api/medicos', payload);
    return data;
  },
  update: async (id: number, doctor: Doctor) => {
    const { data } = await api.put<Doctor>(`/api/medicos/${id}`, doctor);
    return data;
  },
  delete: async (id: number) => {
    await api.delete(`/api/medicos/${id}`);
  },
  deleteForce: async (id: number) => {
    await api.delete(`/api/medicos/${id}/force`);
  },
  getSchedule: async (id: number) => {
      const { data } = await api.get(`/api/medicos/${id}/citas`);
      return data;
  },
  scheduleAppointment: async (cita: any) => {
    const { data } = await api.post('/api/medicos/agendar-cita', cita);
    return data;
  },
  registerDiagnosis: async (diagnostico: any) => {
    const { data } = await api.post('/api/medicos/registrar-diagnostico', diagnostico);
    return data;
  }
};
