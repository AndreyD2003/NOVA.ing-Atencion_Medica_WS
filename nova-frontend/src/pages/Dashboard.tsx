import { useEffect, useState } from 'react';
import { Users, Stethoscope, Calendar, Activity, Loader2 } from 'lucide-react';
import { patientService } from '@/services/patient.service';
import { doctorService } from '@/services/doctor.service';
import { appointmentService } from '@/services/appointment.service';
import { diagnosisService } from '@/services/diagnosis.service';

interface Stats {
  patients: number;
  doctors: number;
  appointments: number;
  diagnoses: number;
}

export const Dashboard = () => {
  const [stats, setStats] = useState<Stats>({
    patients: 0,
    doctors: 0,
    appointments: 0,
    diagnoses: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      // In a real scenario, we might have a dedicated endpoint for dashboard stats
      // For now, we fetch all lists and count them (not efficient for large datasets, but works for MVP)
      const [patients, doctors, appointments, diagnoses] = await Promise.all([
        patientService.getAll(),
        doctorService.getAll(),
        appointmentService.getAll(),
        diagnosisService.getAll()
      ]);

      // Filter appointments for "Today"
      const today = new Date().toISOString().split('T')[0];
      const todayAppointments = appointments.filter(a => a.fechaCita.startsWith(today));

      setStats({
        patients: patients.length,
        doctors: doctors.length,
        appointments: todayAppointments.length,
        diagnoses: diagnoses.length
      });
    } catch (error) {
      console.error("Error loading dashboard stats", error);
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    { name: 'Pacientes', value: stats.patients, icon: Users, color: 'text-blue-600', bg: 'bg-blue-100' },
    { name: 'Médicos', value: stats.doctors, icon: Stethoscope, color: 'text-green-600', bg: 'bg-green-100' },
    { name: 'Citas Hoy', value: stats.appointments, icon: Calendar, color: 'text-purple-600', bg: 'bg-purple-100' },
    { name: 'Diagnósticos', value: stats.diagnoses, icon: Activity, color: 'text-red-600', bg: 'bg-red-100' },
  ];

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.name} className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center">
              <div className={`p-3 rounded-full ${stat.bg} mr-4`}>
                <Icon className={`w-6 h-6 ${stat.color}`} />
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">{stat.name}</p>
                <p className="text-2xl font-semibold text-gray-800">{stat.value}</p>
              </div>
            </div>
          );
        })}
      </div>
      
      <div className="mt-8 bg-white p-6 rounded-lg shadow-sm border border-gray-100">
        <h2 className="text-lg font-semibold text-gray-800 mb-4">Bienvenido a NOVA</h2>
        <p className="text-gray-600">
          Sistema de Gestión Médica Inteligente con capacidades de Web Semántica.
          Utilice el menú lateral para navegar entre los módulos.
        </p>
      </div>
    </div>
  );
};

