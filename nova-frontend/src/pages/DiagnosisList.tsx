import { useEffect, useState } from 'react';
import { diagnosisService, Diagnosis } from '@/services/diagnosis.service';
import { Loader2, Plus, Edit, Trash2, Trash } from 'lucide-react';
import { DiagnosisFormModal } from '@/components/modals/DiagnosisFormModal';
import { ConfirmationModal } from '@/components/modals/ConfirmationModal';

export const DiagnosisList = () => {
  const [diagnoses, setDiagnoses] = useState<Diagnosis[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingDiagnosis, setEditingDiagnosis] = useState<Diagnosis | null>(null);

  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [diagnosisToDelete, setDiagnosisToDelete] = useState<number | null>(null);
  const [deleteType, setDeleteType] = useState<'soft' | 'force'>('soft');

  useEffect(() => {
    loadDiagnoses();
  }, []);

  const loadDiagnoses = () => {
    setLoading(true);
    diagnosisService.getAll()
      .then(setDiagnoses)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const handleCreate = async (data: Diagnosis) => {
    try {
      await diagnosisService.create(data);
      loadDiagnoses();
      setIsFormOpen(false);
    } catch (error) {
      console.error(error);
      alert('Error al crear diagnóstico');
    }
  };

  const handleUpdate = async (data: Diagnosis) => {
    if (!editingDiagnosis?.id) return;
    try {
      await diagnosisService.update(editingDiagnosis.id, data);
      loadDiagnoses();
      setIsFormOpen(false);
      setEditingDiagnosis(null);
    } catch (error) {
      console.error(error);
      alert('Error al actualizar diagnóstico');
    }
  };

  const confirmDelete = (id: number, type: 'soft' | 'force') => {
    setDiagnosisToDelete(id);
    setDeleteType(type);
    setIsDeleteOpen(true);
  };

  const handleDelete = async () => {
    if (!diagnosisToDelete) return;
    try {
      if (deleteType === 'soft') {
        await diagnosisService.delete(diagnosisToDelete);
      } else {
        await diagnosisService.deleteForce(diagnosisToDelete);
      }
      loadDiagnoses();
      setIsDeleteOpen(false);
      setDiagnosisToDelete(null);
    } catch (error) {
      console.error(error);
      alert('Error al eliminar diagnóstico');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Gestión de Diagnósticos</h1>
        <button 
          onClick={() => {
            setEditingDiagnosis(null);
            setIsFormOpen(true);
          }}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center hover:bg-blue-700"
        >
          <Plus className="w-4 h-4 mr-2" />
          Nuevo Diagnóstico
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tipo</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Descripción</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cita (ID)</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {diagnoses.map((diagnosis) => (
                <tr key={diagnosis.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{diagnosis.fechaDiagnostico}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{diagnosis.tipoDiagnostico}</td>
                  <td className="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">{diagnosis.descripcion}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{diagnosis.citaId}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button 
                      onClick={() => {
                        setEditingDiagnosis(diagnosis);
                        setIsFormOpen(true);
                      }}
                      className="text-blue-600 hover:text-blue-900 mr-4"
                      title="Editar"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => diagnosis.id && confirmDelete(diagnosis.id, 'soft')} 
                      className="text-red-600 hover:text-red-900 mr-4"
                      title="Eliminar (Soft)"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                    <button 
                      onClick={() => diagnosis.id && confirmDelete(diagnosis.id, 'force')} 
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

      <DiagnosisFormModal 
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={editingDiagnosis ? handleUpdate : handleCreate}
        initialData={editingDiagnosis}
        title={editingDiagnosis ? 'Editar Diagnóstico' : 'Nuevo Diagnóstico'}
      />

      <ConfirmationModal
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
        title={deleteType === 'soft' ? "Eliminar Diagnóstico" : "Eliminar Permanentemente"}
        message={deleteType === 'soft' 
          ? "¿Estás seguro de eliminar este diagnóstico? Podrá ser recuperado." 
          : "¡ADVERTENCIA! Esta acción eliminará permanentemente al diagnóstico y NO se puede deshacer. ¿Continuar?"}
        variant={deleteType === 'soft' ? 'warning' : 'danger'}
        confirmText="Eliminar"
      />
    </div>
  );
};
