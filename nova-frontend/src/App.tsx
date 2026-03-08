import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { Dashboard } from '@/pages/Dashboard';
import { SemanticChat } from '@/pages/SemanticChat';
import { PatientList } from '@/pages/PatientList';
import { DoctorList } from '@/pages/DoctorList';
import { AppointmentList } from '@/pages/AppointmentList';
import { DiagnosisList } from '@/pages/DiagnosisList';
import { SemanticChatProvider } from '@/context/SemanticChatContext';

function App() {
  return (
    <BrowserRouter>
      <SemanticChatProvider>
        <Routes>
          <Route path="/" element={<AppLayout />}>
            <Route index element={<Dashboard />} />
            <Route path="semantic" element={<SemanticChat />} />
            <Route path="pacientes" element={<PatientList />} />
            <Route path="medicos" element={<DoctorList />} />
            <Route path="citas" element={<AppointmentList />} />
            <Route path="diagnosticos" element={<DiagnosisList />} />
            
            <Route path="pacientes/nuevo" element={<div className="p-4"><h1>Formulario Nuevo Paciente (En construcción)</h1></div>} />
            <Route path="medicos/nuevo" element={<div className="p-4"><h1>Formulario Nuevo Médico (En construcción)</h1></div>} />
            <Route path="citas/nueva" element={<div className="p-4"><h1>Formulario Nuevo Cita (En construcción)</h1></div>} />
            <Route path="diagnosticos/nuevo" element={<div className="p-4"><h1>Formulario Nuevo Diagnóstico (En construcción)</h1></div>} />
          </Route>
        </Routes>
      </SemanticChatProvider>
    </BrowserRouter>
  );
}

export default App;
