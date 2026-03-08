import { useEffect, useState } from 'react';
import { doctorService, Doctor } from '@/services/doctor.service';
import { Loader2, Plus, Edit, Trash2, Search, Trash } from 'lucide-react';
import { DoctorFormModal } from '@/components/modals/DoctorFormModal';
import { ConfirmationModal } from '@/components/modals/ConfirmationModal';

export const DoctorList = () => {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingDoctor, setEditingDoctor] = useState<Doctor | null>(null);

  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [doctorToDelete, setDoctorToDelete] = useState<number | null>(null);
  const [deleteType, setDeleteType] = useState<'soft' | 'force'>('soft');

  useEffect(() => {
    loadDoctors();
  }, []);

  const loadDoctors = () => {
    setLoading(true);
    doctorService.getAll()
      .then(setDoctors)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const handleCreate = async (data: Doctor) => {
    try {
      await doctorService.create(data);
      loadDoctors();
      setIsFormOpen(false);
    } catch (error) {
      console.error(error);
      alert('Error al crear médico');
    }
  };

  const handleUpdate = async (data: Doctor) => {
    if (!editingDoctor?.id) return;
    try {
      await doctorService.update(editingDoctor.id, data);
      loadDoctors();
      setIsFormOpen(false);
      setEditingDoctor(null);
    } catch (error) {
      console.error(error);
      alert('Error al actualizar médico');
    }
  };

  const confirmDelete = (id: number, type: 'soft' | 'force') => {
    setDoctorToDelete(id);
    setDeleteType(type);
    setIsDeleteOpen(true);
  };

  const handleDelete = async () => {
    if (!doctorToDelete) return;
    try {
      if (deleteType === 'soft') {
        await doctorService.delete(doctorToDelete);
      } else {
        await doctorService.deleteForce(doctorToDelete);
      }
      loadDoctors();
      setIsDeleteOpen(false);
      setDoctorToDelete(null);
    } catch (error) {
      console.error(error);
      alert('Error al eliminar médico');
    }
  };

  const filteredDoctors = doctors.filter(d => 
    d.nombres.toLowerCase().includes(searchTerm.toLowerCase()) ||
    d.apellidos.toLowerCase().includes(searchTerm.toLowerCase()) ||
    d.especialidad.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const dayLabel: Record<string, string> = {
    MONDAY: 'Lunes',
    TUESDAY: 'Martes',
    WEDNESDAY: 'Miércoles',
    THURSDAY: 'Jueves',
    FRIDAY: 'Viernes',
    SATURDAY: 'Sábado',
    SUNDAY: 'Domingo'
  };

  const formatTime = (time: string) => {
    if (!time) return '';
    return time.length >= 5 ? time.slice(0, 5) : time;
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Gestión de Médicos</h1>
        <button 
          onClick={() => {
            setEditingDoctor(null);
            setIsFormOpen(true);
          }}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center hover:bg-blue-700"
        >
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Médico
        </button>
      </div>

      <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <input 
            type="text" 
            placeholder="Buscar por nombre, apellido o especialidad..." 
            className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Médico</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Especialidad</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Horario</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredDoctors.map((doctor) => (
                <tr key={doctor.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{doctor.nombres} {doctor.apellidos}</div>
                    <div className="text-sm text-gray-500">DNI: {doctor.dni}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{doctor.especialidad}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {doctor.horarios && doctor.horarios.length > 0 ? (
                      <div className="space-y-1">
                        {doctor.horarios.map((horario) => (
                          <div key={horario.id ?? `${horario.diaSemana}-${horario.horaInicio}-${horario.horaFin}`}>
                            {dayLabel[horario.diaSemana] ?? horario.diaSemana}: {formatTime(horario.horaInicio)} - {formatTime(horario.horaFin)}
                          </div>
                        ))}
                      </div>
                    ) : (
                      <span className="text-gray-400">Sin horario</span>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{doctor.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${doctor.estado === 'ACTIVO' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {doctor.estado}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button 
                      onClick={() => {
                        setEditingDoctor(doctor);
                        setIsFormOpen(true);
                      }}
                      className="text-blue-600 hover:text-blue-900 mr-4"
                      title="Editar"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => doctor.id && confirmDelete(doctor.id, 'soft')} 
                      className="text-red-600 hover:text-red-900 mr-4"
                      title="Eliminar (Soft)"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => doctor.id && confirmDelete(doctor.id, 'force')} 
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

      <DoctorFormModal 
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={editingDoctor ? handleUpdate : handleCreate}
        initialData={editingDoctor}
        title={editingDoctor ? 'Editar Médico' : 'Nuevo Médico'}
      />

      <ConfirmationModal
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
        title={deleteType === 'soft' ? "Eliminar Médico" : "Eliminar Permanentemente"}
        message={deleteType === 'soft' 
          ? "¿Estás seguro de eliminar este médico? Podrá ser recuperado." 
          : "¡ADVERTENCIA! Esta acción eliminará permanentemente al médico y NO se puede deshacer. ¿Continuar?"}
        variant={deleteType === 'soft' ? 'warning' : 'danger'}
        confirmText="Eliminar"
      />
    </div>
  );
};
