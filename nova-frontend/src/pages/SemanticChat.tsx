import { useState } from 'react';
import { Send, Bot, User, Loader2, Search, Lightbulb, Trash2, Code2, RefreshCw } from 'lucide-react';
import { semanticService } from '@/services/semantic.service';
import { useSemanticChat } from '@/context/SemanticChatContext';

interface Message {
  role: 'user' | 'bot';
  content: string;
  results?: Record<string, string>[];
  error?: boolean;
}

export const SemanticChat = () => {
  const [input, setInput] = useState('');
  const [sparqlInput, setSparqlInput] = useState('');
  const [activeTab, setActiveTab] = useState<'buscar' | 'sparql'>('buscar');

  // Usar contexto global para persistencia en navegación
  const { messages, setMessages, suggestions } = useSemanticChat();

  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);

  /** Extrae el mensaje de error del backend (ErrorResponse DTO) */
  const extractErrorMessage = (error: any): string => {
    const data = error?.response?.data;
    if (data && typeof data === 'object') {
      // ErrorResponse del GlobalExceptionHandler: { error, message, status, path }
      return `${data.error || 'Error'}: ${data.message || 'Sin detalle'}`;
    }
    if (typeof data === 'string') return data;
    return error?.message || 'Error desconocido';
  };

  /**
   * Carga masiva: sincroniza TODOS los datos de los microservicios al grafo Fuseki.
   * POST /api/v1/semantic/bulk-load
   */
  const handleBulkLoad = async () => {
    setSyncing(true);
    setMessages(prev => [...prev, { role: 'bot', content: '⏳ Iniciando carga masiva al grafo semántico... Esto puede tardar unos segundos.' }]);
    try {
      const msg = await semanticService.bulkLoad();
      setMessages(prev => [...prev, { role: 'bot', content: `✅ ${msg}` }]);
    } catch (error: any) {
      console.error(error);
      setMessages(prev => [...prev, { role: 'bot', content: `❌ ${extractErrorMessage(error)}`, error: true }]);
    } finally {
      setSyncing(false);
    }
  };

  /**
   * Ejecuta búsqueda en lenguaje natural.
   * Backend: GET /api/v1/semantic/buscar?texto=...
   */
  const executeSearch = async (queryText: string) => {
    if (!queryText.trim()) return;

    const userMessage: Message = { role: 'user', content: queryText };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const results = await semanticService.buscar(queryText);
      const resultCount = results.length;

      const botMessage: Message = {
        role: 'bot',
        content: resultCount > 0
          ? `He encontrado ${resultCount} resultado(s) en el grafo semántico para "${queryText}":`
          : `No se encontraron resultados en el grafo para "${queryText}". Intenta con otros términos.\n\nRecuerda que puedes buscar por:\n• DNI (8 dígitos)\n• Especialidad (cardiología, pediatría, etc.)\n• Estado (programada, realizada, cancelada)\n• Fecha (yyyy-MM-dd)`,
        results: resultCount > 0 ? results : undefined,
      };
      setMessages(prev => [...prev, botMessage]);
    } catch (error: any) {
      console.error(error);
      setMessages(prev => [...prev, { role: 'bot', content: `❌ ${extractErrorMessage(error)}`, error: true }]);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Ejecuta consulta SPARQL directa.
   * Backend: POST /api/v1/semantic/sparql  —  { "query": "..." }
   */
  const executeSparql = async () => {
    if (!sparqlInput.trim()) return;

    const userMessage: Message = { role: 'user', content: `[SPARQL]\n${sparqlInput}` };
    setMessages(prev => [...prev, userMessage]);
    setLoading(true);

    try {
      const results = await semanticService.sparql(sparqlInput);
      const resultCount = results.length;

      const botMessage: Message = {
        role: 'bot',
        content: resultCount > 0
          ? `Consulta SPARQL ejecutada correctamente. ${resultCount} resultado(s):`
          : 'La consulta SPARQL se ejecutó correctamente pero no devolvió resultados.',
        results: resultCount > 0 ? results : undefined,
      };
      setMessages(prev => [...prev, botMessage]);
    } catch (error: any) {
      console.error(error);
      setMessages(prev => [...prev, { role: 'bot', content: `❌ ${extractErrorMessage(error)}`, error: true }]);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setMessages([{
      role: 'bot',
      content: 'Bienvenido al Buscador Semántico. Selecciona una sugerencia o escribe tu búsqueda.\n\nPuedes buscar por:\n• DNI del paciente (ej: 70000001)\n• Especialidad (cardiología, pediatría, dermatología, ginecología, medicina general)\n• Estado de cita (programada, realizada, cancelada)\n• Fecha (ej: hoy, 26/02/2024, del 26-02 al 03-03-2024)'
    }]);
  };

  const handleSend = () => executeSearch(input);

  return (
    <div className="flex h-[calc(100vh-10rem)] gap-4">
      {/* Columna Principal: Chat */}
      <div className="flex-1 flex flex-col bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        <div className="p-4 border-b bg-gray-50 flex justify-between items-center">
          <div className="flex items-center gap-2">
            <Search className="w-5 h-5 text-indigo-600" />
            <h2 className="font-semibold text-gray-700">Búsqueda Semántica</h2>
          </div>
          <div className="flex gap-2">
            <button
              onClick={handleClear}
              className="text-xs px-3 py-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-md flex items-center gap-1 transition-colors"
              title="Limpiar historial"
            >
              <Trash2 className="w-3 h-3" />
              Limpiar
            </button>
            <button
              onClick={handleBulkLoad}
              disabled={syncing || loading}
              className="text-xs px-3 py-1.5 bg-indigo-100 text-indigo-700 rounded-md hover:bg-indigo-200 flex items-center gap-1 disabled:opacity-50 transition-colors"
              title="Cargar todos los datos de los microservicios al grafo semántico"
            >
              <RefreshCw className={`w-3 h-3 ${syncing ? 'animate-spin' : ''}`} />
              {syncing ? 'Sincronizando...' : 'Sincronizar Grafo'}
            </button>
          </div>
        </div>

        {/* Messages area */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50/30">
          {messages.map((msg, idx) => (
            <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`flex max-w-[90%] ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
                <div className={`flex-shrink-0 h-8 w-8 rounded-full flex items-center justify-center ${msg.role === 'user' ? 'bg-indigo-600 ml-2' : 'bg-emerald-600 mr-2'}`}>
                  {msg.role === 'user' ? <User className="h-5 w-5 text-white" /> : <Bot className="h-5 w-5 text-white" />}
                </div>
                <div className={`p-3 rounded-lg shadow-sm ${msg.role === 'user' ? 'bg-indigo-50 text-indigo-900 border border-indigo-100' : 'bg-white text-gray-800 border border-gray-100'}`}>
                  <p className="whitespace-pre-wrap text-sm">{msg.content}</p>

                  {msg.results && msg.results.length > 0 && (() => {
                    const columns = Object.keys(msg.results[0]);
                    return (
                      <div className="mt-3 overflow-x-auto rounded border border-gray-200">
                        <table className="min-w-full text-xs border-collapse bg-white">
                          <thead className="bg-gray-50">
                            <tr>
                              {columns.map(col => (
                                <th key={col} className="px-3 py-2 text-left font-semibold text-gray-600 uppercase tracking-wider border-b">
                                  {col.replace(/_/g, ' ')}
                                </th>
                              ))}
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-gray-100">
                            {msg.results!.map((row, i) => (
                              <tr key={i} className="hover:bg-gray-50 transition-colors">
                                {columns.map(col => (
                                  <td key={col} className="px-3 py-2 text-gray-700 whitespace-nowrap">
                                    {row[col] != null
                                      ? (typeof row[col] === 'object' ? JSON.stringify(row[col]) : String(row[col]))
                                      : '—'}
                                  </td>
                                ))}
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    );
                  })()}
                </div>
              </div>
            </div>
          ))}

          {loading && (
            <div className="flex justify-start">
              <div className="flex max-w-[80%] flex-row">
                <div className="flex-shrink-0 h-8 w-8 rounded-full bg-emerald-600 mr-2 flex items-center justify-center">
                  <Bot className="h-5 w-5 text-white" />
                </div>
                <div className="p-3 bg-white border border-gray-100 rounded-lg flex items-center shadow-sm">
                  <Loader2 className="w-4 h-4 animate-spin mr-2 text-indigo-500" />
                  <span className="text-gray-500 text-sm">Consultando grafo semántico...</span>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Input area with tabs */}
        <div className="border-t bg-white">
          {/* Tabs */}
          <div className="flex border-b">
            <button
              onClick={() => setActiveTab('buscar')}
              className={`flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium transition-colors ${activeTab === 'buscar' ? 'text-indigo-700 border-b-2 border-indigo-700 bg-indigo-50/50' : 'text-gray-500 hover:text-gray-700'}`}
            >
              <Search className="w-3.5 h-3.5" />
              Lenguaje Natural
            </button>
            <button
              onClick={() => setActiveTab('sparql')}
              className={`flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium transition-colors ${activeTab === 'sparql' ? 'text-indigo-700 border-b-2 border-indigo-700 bg-indigo-50/50' : 'text-gray-500 hover:text-gray-700'}`}
            >
              <Code2 className="w-3.5 h-3.5" />
              SPARQL Directo
            </button>
          </div>

          <div className="p-4">
            {activeTab === 'buscar' ? (
              <div className="flex gap-2">
                <input
                  type="text"
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                  placeholder="Busca por DNI, especialidad, estado, fecha (ej: 'citas de cardiología programadas')..."
                  className="flex-1 pl-4 pr-10 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-shadow text-sm"
                  disabled={loading}
                />
                <button
                  onClick={handleSend}
                  disabled={loading || !input.trim()}
                  className="px-6 py-2.5 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center shadow-sm transition-all hover:shadow font-medium text-sm"
                >
                  <Search className="w-4 h-4 mr-2" />
                  Buscar
                </button>
              </div>
            ) : (
              <div className="flex flex-col gap-2">
                <textarea
                  value={sparqlInput}
                  onChange={(e) => setSparqlInput(e.target.value)}
                  placeholder={`PREFIX med: <http://org.nova.atencion.medica/ontologia#>\nSELECT ?cita ?fecha ?paciente ?estado\nWHERE {\n  ?cita rdf:type med:Cita .\n  ?cita med:fechaCita ?fecha .\n  ?cita med:estadoCita ?estado .\n  ?cita med:citaAgendadaPara ?pacienteURI .\n  ?pacienteURI med:nombreCompleto ?paciente .\n}`}
                  className="w-full h-32 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-sm font-mono resize-y"
                  disabled={loading}
                />
                <div className="flex justify-end">
                  <button
                    onClick={executeSparql}
                    disabled={loading || !sparqlInput.trim()}
                    className="px-6 py-2.5 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center shadow-sm transition-all hover:shadow font-medium text-sm"
                  >
                    <Send className="w-4 h-4 mr-2" />
                    Ejecutar SPARQL
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Columna Lateral: Sugerencias */}
      <div className="w-80 bg-white rounded-lg shadow-sm border border-gray-100 p-4 flex flex-col">
        <h3 className="font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <Lightbulb className="w-5 h-5 text-amber-500" />
          Sugerencias
        </h3>
        <div className="space-y-3 overflow-y-auto flex-1">
          {suggestions.map((sug, idx) => (
            <button
              key={idx}
              onClick={() => executeSearch(sug.query)}
              className="w-full flex flex-col items-start p-3 bg-indigo-50/50 border border-indigo-100 rounded-lg hover:bg-indigo-50 hover:border-indigo-300 transition-all text-left group"
            >
              <span className="font-medium text-indigo-700 text-sm group-hover:text-indigo-900">
                {sug.titulo}
              </span>
              <span className="text-xs text-gray-500 mt-1 line-clamp-2">{sug.descripcion}</span>
            </button>
          ))}
        </div>

        {/* Info about backend capabilities */}
        <div className="mt-4 pt-4 border-t">
          <p className="text-xs text-gray-400">
            <strong>Motor semántico:</strong> Apache Jena Fuseki
          </p>
          <p className="text-xs text-gray-400 mt-1">
            <strong>Ontología:</strong> med (Atención Médica)
          </p>
          <p className="text-xs text-gray-400 mt-1">
            <strong>Filtros:</strong> DNI, Especialidad, Estado, Fecha
          </p>
        </div>
      </div>
    </div>
  );
};
