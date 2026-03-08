import React, { createContext, useContext, useState, ReactNode } from 'react';
import type { SemanticSuggestion } from '@/types/semantic.types';
import { SEMANTIC_SUGGESTIONS } from '@/services/semantic.service';

interface Message {
  role: 'user' | 'bot';
  content: string;
  results?: Record<string, string>[];
  error?: boolean;
}

interface SemanticChatContextType {
  messages: Message[];
  setMessages: React.Dispatch<React.SetStateAction<Message[]>>;
  suggestions: SemanticSuggestion[];
}

const SemanticChatContext = createContext<SemanticChatContextType | undefined>(undefined);

export const SemanticChatProvider = ({ children }: { children: ReactNode }) => {
  const [messages, setMessages] = useState<Message[]>([
    { role: 'bot', content: 'Bienvenido al Buscador Semántico. Selecciona una sugerencia o escribe tu búsqueda.\n\nPuedes buscar por:\n• DNI del paciente (ej: 70000001)\n• Especialidad (cardiología, pediatría, dermatología, ginecología, medicina general)\n• Estado de cita (programada, realizada, cancelada)\n• Fecha (ej: hoy, 26/02/2024, del 26-02 al 03-03-2024)' }
  ]);

  // Sugerencias estáticas — no requieren llamada al backend
  const suggestions = SEMANTIC_SUGGESTIONS;

  return (
    <SemanticChatContext.Provider value={{ messages, setMessages, suggestions }}>
      {children}
    </SemanticChatContext.Provider>
  );
};

export const useSemanticChat = () => {
  const context = useContext(SemanticChatContext);
  if (!context) {
    throw new Error('useSemanticChat must be used within a SemanticChatProvider');
  }
  return context;
};
