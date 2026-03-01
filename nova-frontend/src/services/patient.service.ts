import { api } from '@/api/axios';

export interface Patient {
  id?: number;
  dni: string;
  nombres: string;
  apellidos: string;
  telefono: string;
  email: string;
  direccion: string;
  fechaNacimiento: string;
  genero: string;
  estado: string;
  usuarioId?: number;
}

export const patientService = {
  getAll: async () => {
    const { data } = await api.get<Patient[]>('/api/pacientes');
    return data;
  },
  getById: async (id: number) => {
    const { data } = await api.get<Patient>(`/api/pacientes/${id}`);
    return data;
  },
  create: async (patient: Patient) => {
    // Backend espera { "paciente": { ...datos } } debido a CrearPacienteDto
    const payload = { paciente: patient };
    const { data } = await api.post<Patient>('/api/pacientes', payload);
    return data;
  },
  update: async (id: number, patient: Patient) => {
    const { data } = await api.put<Patient>(`/api/pacientes/${id}`, patient);
    return data;
  },
  delete: async (id: number) => {
    await api.delete(`/api/pacientes/${id}`);
  },
  deleteForce: async (id: number) => {
    await api.delete(`/api/pacientes/${id}/force`);
  },
  getMedicalHistory: async (id: number) => {
    const { data } = await api.get(`/api/pacientes/${id}/historial-medico`);
    return data;
  },
  getAppointments: async (id: number) => {
    const { data } = await api.get(`/api/pacientes/${id}/citas`);
    return data;
  },
  scheduleAppointment: async (cita: any) => {
    const { data } = await api.post('/api/pacientes/agendar-cita', cita);
    return data;
  },
  updateAppointmentStatus: async (pacienteId: number, citaId: number, status: string) => {
    const { data } = await api.patch(`/api/pacientes/${pacienteId}/citas/${citaId}/estado`, { estado: status });
    return data;
  }
};
