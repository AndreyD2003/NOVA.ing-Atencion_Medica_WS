import { api } from '@/api/axios';

export interface SemanticQueryResult {
  pregunta: string;
  sparql: string | null;
  mensaje: string;
  resultados: any[];
}

export const semanticService = {
  nlpQuery: async (query: string) => {
    // Endpoint: /api/semantic/nlp/query (POST)
    // Body: { "pregunta": "..." }
    const { data } = await api.post<SemanticQueryResult>(`/api/semantic/nlp/query`, { pregunta: query });
    return data;
  },
  
  sparqlQuery: async (query: string) => {
    // Endpoint: /api/semantic/sparql/query (POST)
    // Body: { "query": "..." }
    const { data } = await api.post<any>(`/api/semantic/sparql/query`, { query });
    return data;
  },

  syncData: async () => {
    // Endpoint: /api/semantic/data/sync (POST)
    const { data } = await api.post<string>(`/api/semantic/data/sync`);
    return data;
  }
};
