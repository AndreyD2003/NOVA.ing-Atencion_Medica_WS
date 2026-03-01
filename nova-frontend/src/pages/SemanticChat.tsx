import { useState } from 'react';
import { Send, Bot, User, Loader2, Database, RefreshCw } from 'lucide-react';
import { semanticService } from '@/services/semantic.service';

interface Message {
  role: 'user' | 'bot';
  content: string;
  results?: any[];
  sparql?: string;
  error?: boolean;
}

export const SemanticChat = () => {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<Message[]>([
    { role: 'bot', content: 'Hola, soy NOVA. ¿En qué puedo ayudarte hoy con la información médica?' }
  ]);
  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);

  const handleSync = async () => {
    setSyncing(true);
    try {
      // @ts-ignore
      const msg = await semanticService.syncData();
      setMessages(prev => [...prev, { role: 'bot', content: `✅ ${msg}` }]);
    } catch (error) {
      console.error(error);
      setMessages(prev => [...prev, { role: 'bot', content: '❌ Error al sincronizar los datos.', error: true }]);
    } finally {
      setSyncing(false);
    }
  };

  const handleSend = async () => {
    if (!input.trim()) return;

    const userMessage: Message = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const response = await semanticService.nlpQuery(userMessage.content);
      const botMessage: Message = {
        role: 'bot',
        content: response.mensaje,
        results: response.resultados,
        sparql: response.sparql || undefined
      };
      setMessages(prev => [...prev, botMessage]);
    } catch (error) {
      console.error(error);
      setMessages(prev => [...prev, { role: 'bot', content: 'Lo siento, hubo un error al procesar tu consulta.', error: true }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-[calc(100vh-10rem)] bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
      <div className="p-4 border-b bg-gray-50 flex justify-between items-center">
        <h2 className="font-semibold text-gray-700">Asistente Inteligente</h2>
        <button
          onClick={handleSync}
          disabled={syncing || loading}
          className="text-xs px-3 py-1.5 bg-indigo-100 text-indigo-700 rounded-md hover:bg-indigo-200 flex items-center gap-1 disabled:opacity-50"
          title="Sincronizar datos con microservicios"
        >
          <RefreshCw className={`w-3 h-3 ${syncing ? 'animate-spin' : ''}`} />
          {syncing ? 'Sincronizando...' : 'Sincronizar Datos'}
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((msg, idx) => (
          <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`flex max-w-[80%] ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
              <div className={`flex-shrink-0 h-8 w-8 rounded-full flex items-center justify-center ${msg.role === 'user' ? 'bg-blue-600 ml-2' : 'bg-green-600 mr-2'}`}>
                {msg.role === 'user' ? <User className="h-5 w-5 text-white" /> : <Bot className="h-5 w-5 text-white" />}
              </div>
              <div className={`p-3 rounded-lg ${msg.role === 'user' ? 'bg-blue-100 text-blue-900' : 'bg-gray-100 text-gray-800'}`}>
                <p className="whitespace-pre-wrap">{msg.content}</p>
                
                {msg.sparql && (
                  <div className="mt-2 p-2 bg-gray-800 text-green-400 text-xs font-mono rounded overflow-x-auto">
                    <div className="flex items-center gap-1 mb-1 text-gray-400">
                      <Database className="w-3 h-3" /> SPARQL Query
                    </div>
                    {msg.sparql}
                  </div>
                )}

                {msg.results && msg.results.length > 0 && (
                  <div className="mt-3 overflow-x-auto">
                    <table className="min-w-full text-xs border-collapse bg-white rounded">
                      <thead>
                        <tr className="bg-gray-50 border-b">
                          {Object.keys(msg.results[0]).map(key => (
                            <th key={key} className="px-2 py-1 text-left font-medium text-gray-500 uppercase tracking-wider">{key}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {msg.results.map((row, i) => (
                          <tr key={i} className="border-b last:border-0 hover:bg-gray-50">
                            {Object.values(row).map((val: any, j) => (
                              <td key={j} className="px-2 py-1 text-gray-700 whitespace-nowrap">
                                {typeof val === 'object' ? JSON.stringify(val) : String(val)}
                              </td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
        {loading && (
          <div className="flex justify-start">
             <div className="flex max-w-[80%] flex-row">
               <div className="flex-shrink-0 h-8 w-8 rounded-full bg-green-600 mr-2 flex items-center justify-center">
                 <Bot className="h-5 w-5 text-white" />
               </div>
               <div className="p-3 bg-gray-100 rounded-lg flex items-center">
                 <Loader2 className="w-4 h-4 animate-spin mr-2 text-gray-500" />
                 <span className="text-gray-500 text-sm">Procesando...</span>
               </div>
             </div>
          </div>
        )}
      </div>
      <div className="p-4 border-t bg-gray-50">
        <div className="flex gap-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Pregunta sobre pacientes, médicos o citas..."
            className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            disabled={loading}
          />
          <button
            onClick={handleSend}
            disabled={loading || !input.trim()}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            <Send className="w-4 h-4 mr-2" />
            Enviar
          </button>
        </div>
      </div>
    </div>
  );
};
