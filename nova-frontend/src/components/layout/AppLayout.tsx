import { useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { LayoutDashboard, Users, Stethoscope, Calendar, Activity, BrainCircuit, Menu, X } from 'lucide-react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: (string | undefined | null | false)[]) {
  return twMerge(clsx(inputs));
}

const navItems = [
  { name: 'Dashboard', href: '/', icon: LayoutDashboard },
  { name: 'Pacientes', href: '/pacientes', icon: Users },
  { name: 'Médicos', href: '/medicos', icon: Stethoscope },
  { name: 'Citas', href: '/citas', icon: Calendar },
  { name: 'Diagnósticos', href: '/diagnosticos', icon: Activity },
  { name: 'Web Semántica', href: '/semantic', icon: BrainCircuit },
];

export const AppLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Mobile Sidebar Overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside className={cn(
        "fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-200 ease-in-out lg:translate-x-0 lg:static lg:inset-0",
        sidebarOpen ? "translate-x-0" : "-translate-x-full"
      )}>
        <div className="flex items-center justify-between h-16 px-6 border-b">
          <span className="text-xl font-bold text-blue-600">Atención Médica</span>
          <button onClick={() => setSidebarOpen(false)} className="lg:hidden">
            <X className="w-6 h-6 text-gray-500" />
          </button>
        </div>
        <div className="flex flex-col h-[calc(100%-4rem)] justify-between">
          <nav className="p-4 space-y-1">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.href;
              return (
                <Link
                  key={item.href}
                  to={item.href}
                  className={cn(
                    "flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors",
                    isActive 
                      ? "bg-blue-50 text-blue-700" 
                      : "text-gray-700 hover:bg-gray-50 hover:text-blue-600"
                  )}
                  onClick={() => setSidebarOpen(false)}
                >
                  <Icon className={cn("w-5 h-5 mr-3", isActive ? "text-blue-600" : "text-gray-400")} />
                  {item.name}
                </Link>
              );
            })}
          </nav>
          <div className="p-4 border-t text-center text-xs text-gray-500">
            <p>© {new Date().getFullYear()} NOVA.ing</p>
            <p>Todos los derechos reservados.</p>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="flex items-center justify-between h-16 px-6 bg-white border-b lg:hidden">
          <button onClick={() => setSidebarOpen(true)} className="text-gray-500 focus:outline-none">
            <Menu className="w-6 h-6" />
          </button>
          <span className="text-xl font-bold text-gray-800">Atención Médica</span>
          <div className="w-6" /> {/* Spacer */}
        </header>

        <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
