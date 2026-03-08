package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.Set;

/**
 * Limpiador de texto para extraer nombres propios residuales.
 *
 * Usa SpanishAnalyzer de Lucene que incluye:
 * - Stopwords del español (~350 palabras: "de","la","el","por","con","favor","necesito",etc.)
 * - Stemming español (Light Spanish Stemmer)
 *
 * Adicionalmente filtra palabras de dominio médico/negocio.
 */
@Component
public class TextCleaner {

    /**
     * SpanishAnalyzer: tokeniza + elimina stopwords españoles + aplica stemming.
     * Esto elimina automáticamente palabras como: necesito, quisiera, favor, podrias,
     * gustaria, todas, todos, urgentemente, completo, etc.
     */
    private final Analyzer analyzer = new SpanishAnalyzer();

    /**
     * Solo palabras de DOMINIO MÉDICO / NEGOCIO que el SpanishAnalyzer no conoce.
     * Ya NO necesitamos agregar verbos/preposiciones del español porque el
     * SpanishAnalyzer + stemming los elimina automáticamente.
     */
    private static final Set<String> PALABRAS_DOMINIO = Set.of(
            // Entidades del dominio
            "cit", "citas", "medic", "medicos", "pacient", "pacientes",
            "diagnostic", "diagnosticos", "doctor", "doctores", "dra",
            // Atributos del dominio
            "especialidad", "especialidades", "motiv", "motivos",
            "nombr", "nombres", "apellid", "apellidos", "dni", "email", "telefon",
            // Acciones de búsqueda (stems)
            "list", "listar", "listad", "busc", "buscar",
            "muestram", "muestr", "mostr", "encontr", "consult", "consultar",
            // Conectores de negocio
            "informacion", "info", "detall", "detalles", "dat",
            "asignad", "agendad",
            // Estados (stems — el QueryParser ya los extrae)
            "atend", "realiz", "program", "cancel", "reprogram", "complet", "pendient",
            // Temporales
            "fech", "seman", "mes", "ano",
            // Rankings / disponibilidad
            "ranking", "top", "mejor", "peor", "disponibl",
            "libr", "horari", "agend",
            // Conectores residuales que el stemmer puede dejar
            "entr", "desd", "hast", "durant"
    );

    /**
     * Limpia el texto eliminando stopwords (vía SpanishAnalyzer) y ruido de dominio.
     * @return nombre limpio o null si queda vacío
     */
    public String limpiar(String texto) {
        if (texto == null || texto.isBlank()) return null;

        // Normalizar: minúsculas, sin tildes
        String normalizado = java.text.Normalizer.normalize(texto.toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        StringBuilder resultado = new StringBuilder();

        try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(normalizado))) {
            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                String term = attr.toString();

                // Ignorar: una sola letra, números puros, palabras de dominio
                if (term.length() > 1 && !term.matches("\\d+") && !PALABRAS_DOMINIO.contains(term)) {
                    resultado.append(term).append(" ");
                }
            }
            tokenStream.end();
        } catch (Exception e) {
            return null;
        }

        String finalStr = resultado.toString().trim();
        return finalStr.isEmpty() ? null : finalStr;
    }
}