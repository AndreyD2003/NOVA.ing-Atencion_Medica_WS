import { api } from '@/api/axios';

export interface Appointment {
  id?: number;
  fechaCita: string;
  horaInicio: string;
  horaFin: string;
  motivo: string;
  estado: string;
  pacienteId: number;
  medicoId: number;
}

export const appointmentService = {
  getAll: async () => {
    const { data } = await api.get<Appointment[]>('/api/citas');
    return data;
  },
  getById: async (id: number) => {
    const { data } = await api.get<Appointment>(`/api/citas/${id}`);
    return data;
  },
  getWithDetails: async (id: number) => {
    const { data } = await api.get(`/api/citas/con-detalle/${id}`);
    return data;
  },
  getByPatientId: async (id: number) => {
    const { data } = await api.get<Appointment[]>(`/api/citas/paciente/${id}`);
    return data;
  },
  getByDoctorId: async (id: number) => {
    const { data } = await api.get<Appointment[]>(`/api/citas/medico/${id}`);
    return data;
  },
  create: async (appointment: Appointment) => {
    const { data } = await api.post<Appointment>('/api/citas', appointment);
    return data;
  },
  update: async (id: number, appointment: Appointment) => {
    const { data } = await api.put<Appointment>(`/api/citas/${id}`, appointment);
    return data;
  },
  delete: async (id: number) => {
    await api.delete(`/api/citas/${id}`);
  },
  deleteForce: async (id: number) => {
    await api.delete(`/api/citas/${id}/force`);
  },
  changeStatus: async (id: number, status: string) => {
    // Backend does not support PATCH, so we simulate it with GET + PUT
    const { data: appointment } = await api.get<Appointment>(`/api/citas/${id}`);
    if (appointment) {
      appointment.estado = status;
      const { data } = await api.put<Appointment>(`/api/citas/${id}`, appointment);
      return data;
    }
    throw new Error('Appointment not found');
  }
};
