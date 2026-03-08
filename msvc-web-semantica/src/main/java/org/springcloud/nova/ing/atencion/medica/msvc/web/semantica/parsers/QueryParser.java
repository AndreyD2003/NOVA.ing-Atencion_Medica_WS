package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Motor de interpretación de consultas en lenguaje natural.
 *
 * Extrae de la frase del usuario:
 *  - DNI (8 dígitos)
 *  - Especialidad médica
 *  - Estado de cita
 *  - Fechas (rango o individual, incluidos "hoy", "ayer", "mañana", "semana")
 *  - Hora (HH:mm o HH:mm:ss)
 *  - Intención: Ranking, Disponibilidad o Búsqueda general
 *  - Nombre propio residual (médico o paciente)
 *  - Tipo de diagnóstico
 *
 * Devuelve un {@link ParseResult} inmutable por invocación.
 * Seguro para uso concurrente (sin estado mutable en el singleton).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class QueryParser {

    private final TextCleaner textCleaner;

    // ──────────── Mapas de sinónimos (orden importa) ────────────

    /** Especialidades: clave = fragmento normalizado (sin tildes), valor = enum de negocio.
     *  Orden descendente de longitud para que "medicina general" se evalúe antes que "general". */
    private static final LinkedHashMap<String, String> ESPECIALIDADES = new LinkedHashMap<>();
    static {
        ESPECIALIDADES.put("medicina general",      "MEDICINA_GENERAL");
        ESPECIALIDADES.put("otorrinolaringologi",   "OTORRINOLARINGOLOGIA");
        ESPECIALIDADES.put("endocrinologi",         "ENDOCRINOLOGIA");
        ESPECIALIDADES.put("traumatologi",          "TRAUMATOLOGIA");
        ESPECIALIDADES.put("oftalmologi",           "OFTALMOLOGIA");
        ESPECIALIDADES.put("dermatologi",           "DERMATOLOGIA");
        ESPECIALIDADES.put("cardiologi",            "CARDIOLOGIA");
        ESPECIALIDADES.put("ginecologi",            "GINECOLOGIA");
        ESPECIALIDADES.put("neurologi",             "NEUROLOGIA");
        ESPECIALIDADES.put("psiquiatri",            "PSIQUIATRIA");
        ESPECIALIDADES.put("neumologi",             "NEUMOLOGIA");
        ESPECIALIDADES.put("oncologi",              "ONCOLOGIA");
        ESPECIALIDADES.put("pediatri",              "PEDIATRIA");
        ESPECIALIDADES.put("urologi",               "UROLOGIA");
        ESPECIALIDADES.put("general",               "MEDICINA_GENERAL");
    }

    /** Estados: "reprogramad" ANTES de "programad" para evitar match parcial. */
    private static final LinkedHashMap<String, String> ESTADOS = new LinkedHashMap<>();
    static {
        ESTADOS.put("no asistio",  "NO_ASISTIO");
        ESTADOS.put("reprogramad", "REPROGRAMADA");
        ESTADOS.put("programad",   "PROGRAMADA");
        ESTADOS.put("cancelad",    "CANCELADA");
        ESTADOS.put("realizad",    "REALIZADA");
        ESTADOS.put("pendient",    "PENDIENTE");
        ESTADOS.put("atendid",     "ATENDIDA");
    }

    private static final List<String> RANKING_KEYWORDS       = List.of("ranking", "top", "mejores", "peores");
    private static final List<String> ORDEN_ASC_KEYWORDS     = List.of("menos", "peor", "peores", "menor", "menores");
    private static final List<String> DISPONIBILIDAD_KEYWORDS = List.of("disponib", "dispon", "libre", "libres", "horario", "horarios", "agenda");
    private static final List<String> HISTORIAL_KEYWORDS = List.of(
            "historial", "historia", "todas las citas", "citas del paciente"
    );

    private static final Map<String, String> TIPOS_DIAGNOSTICO = Map.of(
            "presuntiv", "PRESUNTIVO",
            "definitiv", "DEFINITIVO"
    );

    // ──────────── Patrones regex compilados ────────────
    private static final Pattern DNI_PATTERN    = Pattern.compile("(?<!\\d)(\\d{8})(?!\\d)");
    private static final Pattern HORA_PATTERN   = Pattern.compile("\\b(\\d{1,2}:\\d{2}(?::\\d{2})?)\\b");
    
    // Soporta yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy
    private static final Pattern FECHA_PATTERN  = Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2}|\\d{2}[/-]\\d{2}[/-]\\d{4})\\b");
    
    private static final Pattern ANIO_PATTERN   = Pattern.compile("\\b(20\\d{2})\\b"); // año solo: 2023, 2024, etc.
    private static final Pattern LIMITE_PATTERN = Pattern.compile("(?:top|ranking|mejores|peores)\\s+(\\d{1,2})\\b");

    // ──────────── API pública ────────────

    /**
     * Parsea una consulta de texto libre y devuelve un resultado inmutable.
     */
    public ParseResult parse(String texto) {
        if (texto == null || texto.isBlank()) {
            return ParseResult.EMPTY;
        }

        String frase  = normalizar(texto);
        String residuo = frase;
        LocalDate hoy  = LocalDate.now();

        // 1. DNI (8 dígitos exactos, sin dígitos adyacentes)
        String dni = null;
        Matcher mDni = DNI_PATTERN.matcher(frase);
        if (mDni.find()) {
            dni = mDni.group(1);
            residuo = residuo.replace(dni, " ");
        }

        // 2. Hora (HH:mm o HH:mm:ss)
        String hora = null;
        Matcher mHora = HORA_PATTERN.matcher(frase);
        if (mHora.find()) {
            hora = mHora.group(1);
            residuo = residuo.replace(hora, " ");
        }

        // 3. Fechas
        String fechaInicio = null, fechaFin = null;

        if (frase.contains("mes pasado") || frase.contains("mes anterior")) {
            YearMonth mesPasado = YearMonth.from(hoy.minusMonths(1));
            fechaInicio = mesPasado.atDay(1).toString();
            fechaFin    = mesPasado.atEndOfMonth().toString();
            residuo = residuo.replace("mes pasado", " ").replace("mes anterior", " ");
        } else if (frase.contains("semana pasada") || frase.contains("semana anterior")) {
            LocalDate lunesPasado = hoy.minusWeeks(1).with(DayOfWeek.MONDAY);
            fechaInicio = lunesPasado.toString();
            fechaFin    = lunesPasado.plusDays(6).toString();
            residuo = residuo.replace("semana pasada", " ").replace("semana anterior", " ");

        }else if (frase.contains("esta semana") || frase.contains("semana actual")) {
            LocalDate now = LocalDate.now();
            fechaInicio = now.with(java.time.DayOfWeek.MONDAY).toString();
            fechaFin    = now.with(java.time.DayOfWeek.SUNDAY).toString();
            residuo = residuo.replace("esta semana", " ").replace("semana actual", " ");
        } else if (frase.contains("este mes") || frase.contains("mes actual")) {
            LocalDate now = LocalDate.now();
            fechaInicio = now.withDayOfMonth(1).toString();
            fechaFin    = now.withDayOfMonth(now.lengthOfMonth()).toString();
            residuo = residuo.replace("este mes", " ").replace("mes actual", " ");
        } else if (frase.contains("este ano") || frase.contains("ano actual")) {
            fechaInicio = hoy.getYear() + "-01-01";
            fechaFin    = hoy.getYear() + "-12-31";
            residuo = residuo.replace("este ano", " ").replace("ano actual", " ");
        } else if (frase.contains("hoy")) {
            fechaInicio = LocalDate.now().toString();
            residuo = residuo.replace("hoy", " ");
        } else if (frase.contains("ayer")) {
            fechaInicio = LocalDate.now().minusDays(1).toString();
            residuo = residuo.replace("ayer", " ");
        } else if (frase.contains("manana")) {
            fechaInicio = LocalDate.now().plusDays(1).toString();
            residuo = residuo.replace("manana", " ");
        }

        // Fechas explícitas
        List<String> fechasExplicitas = new ArrayList<>();
        Matcher mFecha = FECHA_PATTERN.matcher(frase);
        while (mFecha.find()) {
            String fRaw = mFecha.group(1);
            String fIso = normalizarFechaAIso(fRaw);
            if (fIso != null) {
                fechasExplicitas.add(fIso);
            }
            residuo = residuo.replace(fRaw, " ");
        }
        
        if (fechasExplicitas.size() >= 2) {
            fechaInicio = fechasExplicitas.get(0);
            fechaFin    = fechasExplicitas.get(1);
            
            // Validación lógica: fecha fin no puede ser anterior a fecha inicio
            if (LocalDate.parse(fechaFin).isBefore(LocalDate.parse(fechaInicio))) {
                log.warn("Rango de fechas inválido: {} es anterior a {}. Invirtiendo.", fechaFin, fechaInicio);
                String temp = fechaInicio;
                fechaInicio = fechaFin;
                fechaFin = temp;
            }
        } else if (!fechasExplicitas.isEmpty() && fechaInicio == null) {
            fechaInicio = fechasExplicitas.get(0);
        }

        // Año solo: "citas del 2023" → fechaInicio = "2023" (longitud 4, SparqlBuilder usa YEAR())
        if (fechaInicio == null) {
            Matcher mAnio = ANIO_PATTERN.matcher(frase);
            if (mAnio.find()) {
                fechaInicio = mAnio.group(1); // ej: "2023"
                residuo = residuo.replace(fechaInicio, " ");
            }
        }

        // 4. Especialidad (primera coincidencia — el mapa ya tiene el orden correcto)
        String especialidad = null;
        for (Map.Entry<String, String> entry : ESPECIALIDADES.entrySet()) {
            if (frase.contains(entry.getKey())) {
                especialidad = entry.getValue();
                residuo = residuo.replaceAll(Pattern.quote(entry.getKey()) + "[a-z]*", " ");
                break;
            }
        }

        // 5. Estado (primera coincidencia — "reprogramad" antes que "programad")
        String estado = null;
        for (Map.Entry<String, String> entry : ESTADOS.entrySet()) {
            if (frase.contains(entry.getKey())) {
                estado = entry.getValue();
                residuo = residuo.replaceAll(Pattern.quote(entry.getKey()) + "[a-z]*", " ");
                break;
            }
        }

        // 6. Tipo de diagnóstico
        String tipoDiagnostico = null;
        for (Map.Entry<String, String> entry : TIPOS_DIAGNOSTICO.entrySet()) {
            if (frase.contains(entry.getKey())) {
                tipoDiagnostico = entry.getValue();
                residuo = residuo.replaceAll(Pattern.quote(entry.getKey()) + "[a-z]*", " ");
                break;
            }
        }

        boolean esHistorial = false;
        for (String kw : HISTORIAL_KEYWORDS) {
            if (frase.contains(kw)) {
                esHistorial = true;
                residuo = residuo.replace(kw, " ");
                break;
            }
        }

        // 7. Intención: Ranking
        boolean esRanking = false;
        boolean ordenAsc  = false;
        int limite = 5;

        for (String kw : RANKING_KEYWORDS) {
            if (frase.contains(kw)) {
                esRanking = true;
                residuo = residuo.replace(kw, " ");
                break;
            }
        }
        if (esRanking) {
            for (String kw : ORDEN_ASC_KEYWORDS) {
                if (frase.contains(kw)) { ordenAsc = true; break; }
            }
            Matcher mLim = LIMITE_PATTERN.matcher(frase);
            if (mLim.find()) {
                limite = Integer.parseInt(mLim.group(1));
            }
        }

        // 8. Intención: Disponibilidad
        boolean esDisponibilidad = false;
        if (!esRanking) {
            for (String kw : DISPONIBILIDAD_KEYWORDS) {
                if (frase.contains(kw)) {
                    esDisponibilidad = true;
                    residuo = residuo.replace(kw, " ");
                    break;
                }
            }
        }

        // 9. Nombre propio residual
        String nombre = null;
        if (!esRanking && !esDisponibilidad) {
            nombre = textCleaner.limpiar(residuo);
        }

        ParseResult result = ParseResult.builder()
                .dni(dni)
                .especialidad(especialidad)
                .estado(estado)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .horaBusqueda(hora)
                .nombreBusqueda(nombre)
                .tipoDiagnostico(tipoDiagnostico)
                .esRankingMedicos(esRanking)
                .esBusquedaDisponibilidad(esDisponibilidad)
                .esHistorial(esHistorial)
                .ordenAscendente(ordenAsc)
                .limite(limite)
                .build();

        log.info("Resultado del parsing: {}", result);
        return result;
    }

    // ──────────── Helpers ────────────

    private String normalizar(String t) {
        return java.text.Normalizer.normalize(t.toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Convierte dd/MM/yyyy o dd-MM-yyyy a yyyy-MM-dd.
     * Si ya es yyyy-MM-dd lo mantiene.
     */
    private String normalizarFechaAIso(String f) {
        if (f == null) return null;
        if (f.matches("\\d{4}-\\d{2}-\\d{2}")) return f;
        
        try {
            // Reemplazar / por - para normalizar
            String normalized = f.replace('/', '-');
            String[] parts = normalized.split("-");
            if (parts.length == 3) {
                // Asumimos dd-MM-yyyy
                return String.format("%s-%s-%s", parts[2], parts[1], parts[0]);
            }
        } catch (Exception e) {
            log.warn("No se pudo normalizar la fecha: {}", f);
        }
        return null;
    }
}