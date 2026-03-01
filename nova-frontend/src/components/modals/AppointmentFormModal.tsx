import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { Appointment } from '@/services/appointment.service';
import { Patient, patientService } from '@/services/patient.service';
import { Doctor, doctorService } from '@/services/doctor.service';

interface AppointmentFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: Appointment) => void;
  initialData?: Appointment | null;
  title: string;
}

export const AppointmentFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  title
}: AppointmentFormModalProps) => {
  const [formData, setFormData] = useState<Appointment>({
    fechaCita: '',
    horaInicio: '',
    horaFin: '',
    motivo: '',
    estado: 'PROGRAMADA',
    pacienteId: 0,
    medicoId: 0
  });

  const [patients, setPatients] = useState<Patient[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);

  useEffect(() => {
    if (isOpen) {
      loadDependencies();
      if (initialData) {
        setFormData(initialData);
      } else {
        setFormData({
          fechaCita: '',
          horaInicio: '',
          horaFin: '',
          motivo: '',
          estado: 'PROGRAMADA',
          pacienteId: 0,
          medicoId: 0
        });
      }
    }
  }, [initialData, isOpen]);

  const loadDependencies = async () => {
    try {
      const [patientsData, doctorsData] = await Promise.all([
        patientService.getAll(),
        doctorService.getAll()
      ]);
      setPatients(patientsData);
      setDoctors(doctorsData);
    } catch (error) {
      console.error("Error loading dependencies", error);
    }
  };

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 overflow-y-auto">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4 my-8 animate-in fade-in zoom-in duration-200">
        <div className="flex justify-between items-center p-4 border-b">
          <h3 className="text-lg font-semibold text-gray-900">{title}</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Paciente</label>
              <select
                required
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.pacienteId}
                onChange={e => setFormData({ ...formData, pacienteId: Number(e.target.value) })}
              >
                <option value={0}>Seleccionar Paciente...</option>
                {patients.map(p => (
                  <option key={p.id} value={p.id}>{p.nombres} {p.apellidos} - {p.dni}</option>
                ))}
              </select>
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Médico</label>
              <select
                required
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.medicoId}
                onChange={e => setFormData({ ...formData, medicoId: Number(e.target.value) })}
              >
                <option value={0}>Seleccionar Médico...</option>
                {doctors.map(d => (
                  <option key={d.id} value={d.id}>{d.nombres} {d.apellidos} - {d.especialidad}</option>
                ))}
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Fecha Cita</label>
              <input
                required
                type="date"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.fechaCita?.split('T')[0]}
                onChange={e => setFormData({ ...formData, fechaCita: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.estado}
                onChange={e => setFormData({ ...formData, estado: e.target.value })}
              >
                <option value="PROGRAMADA">PROGRAMADA</option>
                <option value="CONFIRMADA">CONFIRMADA</option>
                <option value="CANCELADA">CANCELADA</option>
                <option value="REALIZADA">REALIZADA</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Hora Inicio</label>
              <input
                required
                type="time"
                step="1"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.horaInicio}
                onChange={e => setFormData({ ...formData, horaInicio: e.target.value })}
              />
            </div>
             <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Hora Fin</label>
              <input
                required
                type="time"
                step="1"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.horaFin}
                onChange={e => setFormData({ ...formData, horaFin: e.target.value })}
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Motivo</label>
              <textarea
                required
                rows={3}
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.motivo}
                onChange={e => setFormData({ ...formData, motivo: e.target.value })}
              />
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
            >
              Guardar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
