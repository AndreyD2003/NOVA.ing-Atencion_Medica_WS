import { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { Patient } from '@/services/patient.service';

interface PatientFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: Patient) => void;
  initialData?: Patient | null;
  title: string;
}

export const PatientFormModal = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  title
}: PatientFormModalProps) => {
  const [formData, setFormData] = useState<Patient>({
    dni: '',
    nombres: '',
    apellidos: '',
    telefono: '',
    email: '',
    direccion: '',
    fechaNacimiento: '',
    genero: '',
    estado: 'ACTIVO'
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        ...initialData,
        fechaNacimiento: initialData.fechaNacimiento?.split('T')[0] || ''
      });
    } else {
      setFormData({
        dni: '',
        nombres: '',
        apellidos: '',
        telefono: '',
        email: '',
        direccion: '',
        fechaNacimiento: '',
        genero: '',
        estado: 'ACTIVO'
      });
    }
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const dataToSubmit = {
      ...formData,
      // Asegurar que usuarioId sea null si no existe, o un valor por defecto si es requerido por el backend
      usuarioId: formData.usuarioId || 1 
    };
    onSubmit(dataToSubmit);
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
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">DNI</label>
              <input
                required
                type="text"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.dni}
                onChange={e => setFormData({ ...formData, dni: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estado</label>
              <select
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.estado}
                onChange={e => setFormData({ ...formData, estado: e.target.value })}
              >
                <option value="ACTIVO">ACTIVO</option>
                <option value="INACTIVO">INACTIVO</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Apellidos</label>
              <input
                required
                type="text"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.apellidos}
                onChange={e => setFormData({ ...formData, apellidos: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nombres</label>
              <input
                required
                type="text"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.nombres}
                onChange={e => setFormData({ ...formData, nombres: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input
                required
                type="email"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.email}
                onChange={e => setFormData({ ...formData, email: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
              <input
                required
                type="tel"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.telefono}
                onChange={e => setFormData({ ...formData, telefono: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Fecha Nacimiento</label>
              <input
                required
                type="date"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.fechaNacimiento?.split('T')[0]} // Handle date format
                onChange={e => setFormData({ ...formData, fechaNacimiento: e.target.value })}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Género</label>
              <select
                required
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.genero}
                onChange={e => setFormData({ ...formData, genero: e.target.value })}
              >
                <option value="">Seleccionar...</option>
                <option value="MASCULINO">MASCULINO</option>
                <option value="FEMENINO">FEMENINO</option>
              </select>
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Dirección</label>
              <input
                required
                type="text"
                className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                value={formData.direccion}
                onChange={e => setFormData({ ...formData, direccion: e.target.value })}
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
