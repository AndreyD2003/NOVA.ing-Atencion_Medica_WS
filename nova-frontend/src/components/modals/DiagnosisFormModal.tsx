import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { Diagnosis } from '@/services/diagnosis.service';
import { Appointment, appointmentService } from '@/services/appointment.service';
import { Patient, patientService } from '@/services/patient.service';

interface DiagnosisFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: Diagnosis) => void;
  initialData?: Diagnosis | null;
  title: string;
}

export const DiagnosisFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  title
}: DiagnosisFormModalProps) => {
  const [formData, setFormData] = useState<Diagnosis>({
    descripcion: '',
    tipoDiagnostico: 'PRESUNTIVO',
    fechaDiagnostico: new Date().toISOString().split('T')[0],
    citaId: 0,
    pacienteId: 0
  });

  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);

  useEffect(() => {
    if (isOpen) {
      loadDependencies();
      if (initialData) {
        setFormData(initialData);
      } else {
        setFormData({
          descripcion: '',
          tipoDiagnostico: 'PRESUNTIVO',
          fechaDiagnostico: new Date().toISOString().split('T')[0],
          citaId: 0,
          pacienteId: 0
        });
      }
    }
  }, [initialData, isOpen]);

  const loadDependencies = async () => {
    try {
        // Ideally we should filter appointments to pick one, but for now load all
        const [appData, patData] = await Promise.all([
            appointmentService.getAll(),
            patientService.getAll()
        ]);
        setAppointments(appData);
        setPatients(patData);
    } catch (e) {
        console.error("Error loading dependencies", e);
    }
  }

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
              <label className="block text-sm font-medium text-gray-700 mb-1">Cita Asociada</label>
              <select
                required
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.citaId}
                onChange={e => {
                    const citaId = Number(e.target.value);
                    const cita = appointments.find(a => a.id === citaId);
                    setFormData({ 
                        ...formData, 
                        citaId, 
                        pacienteId: cita ? cita.pacienteId : 0 
                    });
                }}
              >
                <option value={0}>Seleccionar Cita...</option>
                {appointments.map(a => (
                  <option key={a.id} value={a.id}>Cita #{a.id} - {a.fechaCita}</option>
                ))}
              </select>
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Paciente (Automático desde Cita)</label>
              <select
                disabled
                className="w-full p-2 border rounded-md bg-gray-100"
                value={formData.pacienteId}
              >
                 <option value={0}>Seleccionar Paciente...</option>
                 {patients.map(p => (
                     <option key={p.id} value={p.id}>{p.nombres} {p.apellidos}</option>
                 ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Fecha Diagnóstico</label>
              <input
                required
                type="date"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.fechaDiagnostico?.split('T')[0]}
                onChange={e => setFormData({ ...formData, fechaDiagnostico: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
              <select
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.tipoDiagnostico}
                onChange={e => setFormData({ ...formData, tipoDiagnostico: e.target.value })}
              >
                <option value="PRESUNTIVO">PRESUNTIVO</option>
                <option value="DEFINITIVO">DEFINITIVO</option>
              </select>
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
              <textarea
                required
                rows={4}
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.descripcion}
                onChange={e => setFormData({ ...formData, descripcion: e.target.value })}
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
