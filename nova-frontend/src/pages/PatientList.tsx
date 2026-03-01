import { useEffect, useState } from 'react';
import { patientService, Patient } from '@/services/patient.service';
import { Loader2, Plus, Edit, Trash2, Search, Trash } from 'lucide-react';
import { PatientFormModal } from '@/components/modals/PatientFormModal';
import { ConfirmationModal } from '@/components/modals/ConfirmationModal';

export const PatientList = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPatient, setEditingPatient] = useState<Patient | null>(null);
  
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [patientToDelete, setPatientToDelete] = useState<number | null>(null);
  const [deleteType, setDeleteType] = useState<'soft' | 'force'>('soft');

  useEffect(() => {
    loadPatients();
  }, []);

  const loadPatients = () => {
    setLoading(true);
    patientService.getAll()
      .then(setPatients)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const handleCreate = async (data: Patient) => {
    try {
      await patientService.create(data);
      loadPatients();
      setIsFormOpen(false);
    } catch (error) {
      console.error(error);
      alert('Error al crear paciente');
    }
  };

  const handleUpdate = async (data: Patient) => {
    if (!editingPatient?.id) return;
    try {
      await patientService.update(editingPatient.id, data);
      loadPatients();
      setIsFormOpen(false);
      setEditingPatient(null);
    } catch (error) {
      console.error(error);
      alert('Error al actualizar paciente');
    }
  };

  const confirmDelete = (id: number, type: 'soft' | 'force') => {
    setPatientToDelete(id);
    setDeleteType(type);
    setIsDeleteOpen(true);
  };

  const handleDelete = async () => {
    if (!patientToDelete) return;
    try {
      if (deleteType === 'soft') {
        await patientService.delete(patientToDelete);
      } else {
        await patientService.deleteForce(patientToDelete);
      }
      loadPatients();
      setIsDeleteOpen(false);
      setPatientToDelete(null);
    } catch (error) {
      console.error(error);
      alert('Error al eliminar paciente');
    }
  };

  const filteredPatients = patients.filter(p => 
    p.nombres.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.apellidos.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.dni.includes(searchTerm)
  );

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Gestión de Pacientes</h1>
        <button 
          onClick={() => {
            setEditingPatient(null);
            setIsFormOpen(true);
          }}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center hover:bg-blue-700"
        >
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Paciente
        </button>
      </div>

      <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <input 
            type="text" 
            placeholder="Buscar por nombre, apellido o DNI..." 
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Paciente</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">DNI</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Contacto</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredPatients.map((patient) => (
                <tr key={patient.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{patient.nombres} {patient.apellidos}</div>
                    <div className="text-sm text-gray-500">{patient.genero}, {patient.fechaNacimiento}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{patient.dni}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">{patient.email}</div>
                    <div className="text-sm text-gray-500">{patient.telefono}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${patient.estado === 'ACTIVO' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                      {patient.estado}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button 
                      onClick={() => {
                        setEditingPatient(patient);
                        setIsFormOpen(true);
                      }}
                      className="text-blue-600 hover:text-blue-900 mr-4"
                      title="Editar"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => patient.id && confirmDelete(patient.id, 'soft')} 
                      className="text-red-600 hover:text-red-900 mr-4"
                      title="Eliminar (Soft)"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                     <button 
                      onClick={() => patient.id && confirmDelete(patient.id, 'force')} 
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

      <PatientFormModal 
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={editingPatient ? handleUpdate : handleCreate}
        initialData={editingPatient}
        title={editingPatient ? 'Editar Paciente' : 'Nuevo Paciente'}
      />

      <ConfirmationModal
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
        title={deleteType === 'soft' ? "Eliminar Paciente" : "Eliminar Permanentemente"}
        message={deleteType === 'soft' 
          ? "¿Estás seguro de eliminar este paciente? Podrá ser recuperado." 
          : "¡ADVERTENCIA! Esta acción eliminará permanentemente al paciente y NO se puede deshacer. ¿Continuar?"}
        variant={deleteType === 'soft' ? 'warning' : 'danger'}
        confirmText="Eliminar"
      />
    </div>
  );
};
