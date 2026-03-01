import { useEffect, useState } from 'react';
import { appointmentService, Appointment } from '@/services/appointment.service';
import { Loader2, Plus, Edit, Trash2, Calendar, Trash } from 'lucide-react';
import { AppointmentFormModal } from '@/components/modals/AppointmentFormModal';
import { ConfirmationModal } from '@/components/modals/ConfirmationModal';

export const AppointmentList = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState<Appointment | null>(null);

  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [appointmentToDelete, setAppointmentToDelete] = useState<number | null>(null);
  const [deleteType, setDeleteType] = useState<'soft' | 'force'>('soft');

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = () => {
    setLoading(true);
    appointmentService.getAll()
      .then(setAppointments)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const handleCreate = async (data: Appointment) => {
    try {
      await appointmentService.create(data);
      loadAppointments();
      setIsFormOpen(false);
    } catch (error) {
      console.error(error);
      alert('Error al crear cita');
    }
  };

  const handleUpdate = async (data: Appointment) => {
    if (!editingAppointment?.id) return;
    try {
      await appointmentService.update(editingAppointment.id, data);
      loadAppointments();
      setIsFormOpen(false);
      setEditingAppointment(null);
    } catch (error) {
      console.error(error);
      alert('Error al actualizar cita');
    }
  };

  const confirmDelete = (id: number, type: 'soft' | 'force') => {
    setAppointmentToDelete(id);
    setDeleteType(type);
    setIsDeleteOpen(true);
  };

  const handleDelete = async () => {
    if (!appointmentToDelete) return;
    try {
      if (deleteType === 'soft') {
        await appointmentService.delete(appointmentToDelete);
      } else {
        await appointmentService.deleteForce(appointmentToDelete);
      }
      loadAppointments();
      setIsDeleteOpen(false);
      setAppointmentToDelete(null);
    } catch (error) {
      console.error(error);
      alert('Error al eliminar cita');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Gestión de Citas</h1>
        <button 
          onClick={() => {
            setEditingAppointment(null);
            setIsFormOpen(true);
          }}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center hover:bg-blue-700"
        >
          <Plus className="w-4 h-4 mr-2" />
          Nueva Cita
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        {loading ? (
          <div className="p-8 flex justify-center">
            <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
          </div>
        ) : (
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha y Hora</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Motivo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Paciente/Médico (IDs)</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {appointments.map((appointment) => (
                <tr key={appointment.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center text-sm text-gray-900">
                      <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                      {appointment.fechaCita}
                    </div>
                    <div className="text-sm text-gray-500 ml-6">{appointment.horaInicio} - {appointment.horaFin}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{appointment.motivo}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                      appointment.estado === 'PROGRAMADA' ? 'bg-blue-100 text-blue-800' : 
                      appointment.estado === 'REALIZADA' ? 'bg-green-100 text-green-800' : 
                      appointment.estado === 'CANCELADA' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {appointment.estado}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div>P: {appointment.pacienteId}</div>
                    <div>M: {appointment.medicoId}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button 
                      onClick={() => {
                        setEditingAppointment(appointment);
                        setIsFormOpen(true);
                      }}
                      className="text-blue-600 hover:text-blue-900 mr-4"
                      title="Editar"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => appointment.id && confirmDelete(appointment.id, 'soft')} 
                      className="text-red-600 hover:text-red-900 mr-4"
                      title="Eliminar (Soft)"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => appointment.id && confirmDelete(appointment.id, 'force')} 
                      className="text-red-800 hover:text-red-950"
                      title="Eliminar Permanente"
                    >
                      <Trash className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <AppointmentFormModal 
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={editingAppointment ? handleUpdate : handleCreate}
        initialData={editingAppointment}
        title={editingAppointment ? 'Editar Cita' : 'Nueva Cita'}
      />

      <ConfirmationModal
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
        title={deleteType === 'soft' ? "Eliminar Cita" : "Eliminar Permanentemente"}
        message={deleteType === 'soft' 
          ? "¿Estás seguro de eliminar esta cita? Podrá ser recuperada." 
          : "¡ADVERTENCIA! Esta acción eliminará permanentemente la cita y NO se puede deshacer. ¿Continuar?"}
        variant={deleteType === 'soft' ? 'warning' : 'danger'}
        confirmText="Eliminar"
      />
    </div>
  );
};
