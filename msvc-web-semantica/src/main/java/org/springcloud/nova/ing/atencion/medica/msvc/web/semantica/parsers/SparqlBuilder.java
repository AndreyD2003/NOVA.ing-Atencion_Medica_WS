package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.vocabulary.MED;
import org.springframework.stereotype.Component;

/**
 * Genera queries SPARQL a partir de un {@link ParseResult}.
 * Compatible con Apache Jena Fuseki (sin GROUP_CONCAT ni BIND/IF).
 */
@Component
public class SparqlBuilder {

    private static final String PREFIX =
            "PREFIX med: <" + MED.NS + ">\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";

    // ──────────── Punto de entrada ────────────

    public String buildSearchQuery(ParseResult p) {
        if (p == null || p == ParseResult.EMPTY) {
            return buildGeneralQuery(ParseResult.EMPTY);
        }
        if (p.isEsBusquedaDisponibilidad() && p.getFechaInicio() != null) {
            return buildAvailabilityQuery(p);
        }
        if (p.isEsRankingMedicos()) {
            return buildRankingQuery(p);
        }
        // Si hay DNI y la frase contiene "historial" o "citas de"
        // el parser ya habrá seteado esHistorial = true
        if (p.isEsHistorial() && p.getDni() != null) {
            return buildHistorialQuery(p);
        }
        return buildGeneralQuery(p);
    }

    // ──────────── 1. Disponibilidad ────────────

    private String buildAvailabilityQuery(ParseResult p) {
        StringBuilder sb = new StringBuilder(PREFIX);
        sb.append("SELECT DISTINCT ?medico ?especialidad ?dni_medico WHERE {\n");
        sb.append("  ?mUri rdf:type med:Medico ;\n");
        sb.append("        med:nombreMedico ?medico ;\n");
        sb.append("        med:especialidad ?especialidad .\n");
        sb.append("  OPTIONAL { ?mUri med:dniMedico ?dni_medico }\n");
        sb.append("  FILTER NOT EXISTS {\n");
        sb.append("    ?cita med:atendidaPor ?mUri ;\n");
        sb.append("          med:fechaCita ?fCita .\n");
        sb.append(buildFechaFilter("?fCita", p));
        sb.append("  }\n");
        if (p.getEspecialidad() != null) {
            sb.append(String.format("  FILTER(LCASE(STR(?especialidad)) = \"%s\")\n",
                    p.getEspecialidad().toLowerCase()));
        }
        sb.append("}\nORDER BY ?especialidad ?medico\nLIMIT 20\n");
        return sb.toString();
    }

    // ──────────── 2. Ranking ────────────

    private String buildRankingQuery(ParseResult p) {
        StringBuilder sb = new StringBuilder(PREFIX);
        sb.append("SELECT ?medico ?especialidad (COUNT(DISTINCT ?cita) AS ?total_citas) WHERE {\n");
        sb.append("  ?cita rdf:type med:Cita ;\n");
        sb.append("        med:atendidaPor ?mUri .\n");
        sb.append("  ?mUri med:nombreMedico ?medico ;\n");
        sb.append("        med:especialidad ?especialidad .\n");

        if (p.getFechaInicio() != null) {
            sb.append("  ?cita med:fechaCita ?fecha .\n");
        }

        if (p.getEspecialidad() != null) {
            sb.append(String.format("  FILTER(LCASE(STR(?especialidad)) = \"%s\")\n",
                    p.getEspecialidad().toLowerCase()));
        }
        appendDateFilters(sb, p);

        sb.append("}\nGROUP BY ?medico ?especialidad\n");
        sb.append(String.format("ORDER BY %s(?total_citas)\n", p.isOrdenAscendente() ? "ASC" : "DESC"));
        sb.append("LIMIT ").append(Math.max(p.getLimite(), 1)).append("\n");
        return sb.toString();
    }

    // ──────────── 4. Historial por Paciente o Médico ────────────

    private String buildHistorialQuery(ParseResult p) {
        StringBuilder sb = new StringBuilder(PREFIX);
        sb.append("SELECT DISTINCT ?fecha ?estado ?paciente ?dni_paciente ?medico ?dni_medico ?especialidad ?motivo ?diagnostico WHERE {\n");

        // Cita (núcleo obligatorio)
        sb.append("  ?cita rdf:type med:Cita ;\n");
        sb.append("        med:fechaCita ?fecha ;\n");
        sb.append("        med:estadoCita ?estado ;\n");
        sb.append("        med:citaAgendadaPara ?pUri ;\n");
        sb.append("        med:atendidaPor ?mUri .\n");

        // Paciente
        sb.append("  ?pUri med:nombreCompleto ?paciente .\n");
        sb.append("  OPTIONAL { ?pUri med:dniPaciente ?dni_paciente }\n");

        // Médico
        sb.append("  ?mUri med:nombreMedico ?medico ;\n");
        sb.append("        med:especialidad ?especialidad .\n");
        sb.append("  OPTIONAL { ?mUri med:dniMedico ?dni_medico }\n");

        // Motivo
        sb.append("  OPTIONAL { ?cita med:motivoCita ?motivo }\n");

        // Diagnóstico
        sb.append("  OPTIONAL { ?cita med:generaDiagnostico ?dUri . ?dUri med:descripcionDiag ?diagnostico }\n");

        if (p.getDni() != null) {
            sb.append(String.format("  FILTER(COALESCE(STR(?dni_paciente), \"\") = \"%s\" || COALESCE(STR(?dni_medico), \"\") = \"%s\")\n",
                    p.getDni(), p.getDni()));
        }

        if (p.getEstado() != null) {
            sb.append(String.format(
                    "  FILTER(LCASE(STR(?estado)) = \"%s\")\n",
                    p.getEstado().toLowerCase()
            ));
        }

        appendDateFilters(sb, p);

        sb.append("}\nORDER BY DESC(?fecha)\nLIMIT 50\n");
        return sb.toString();
    }

    // ──────────── 3. Búsqueda General (sin GROUP_CONCAT) ────────────

    private String buildGeneralQuery(ParseResult p) {
        StringBuilder sb = new StringBuilder(PREFIX);

        sb.append("SELECT DISTINCT ?fecha ?estado ?paciente ?dni_paciente ");
        sb.append("?medico ?dni_medico ?especialidad ?motivo ?diagnostico\n");
        sb.append("WHERE {\n");

        // Cita (núcleo obligatorio)
        sb.append("  ?cita rdf:type med:Cita ;\n");
        sb.append("        med:fechaCita ?fecha ;\n");
        sb.append("        med:estadoCita ?estado ;\n");
        sb.append("        med:citaAgendadaPara ?pUri ;\n");
        sb.append("        med:atendidaPor ?mUri .\n");

        // Paciente
        sb.append("  ?pUri med:nombreCompleto ?paciente .\n");
        // Siempre usamos OPTIONAL para evitar descartar citas si falta un dato
        sb.append("  OPTIONAL { ?pUri med:dniPaciente ?dni_paciente }\n");

        // Médico
        sb.append("  ?mUri med:nombreMedico ?medico ;\n");
        sb.append("        med:especialidad ?especialidad .\n");
        sb.append("  OPTIONAL { ?mUri med:dniMedico ?dni_medico }\n");

        // Motivo
        sb.append("  OPTIONAL { ?cita med:motivoCita ?motivo }\n");

        // Diagnóstico
        sb.append("  OPTIONAL { ?cita med:generaDiagnostico ?dUri . ?dUri med:descripcionDiag ?diagnostico }\n");

        // ── Filtros dinámicos ──
        if (p.getDni() != null) {
            // Se busca el DNI en paciente O médico. COALESCE evita errores con variables unbound.
            sb.append(String.format("  FILTER(COALESCE(STR(?dni_paciente), \"\") = \"%s\" || COALESCE(STR(?dni_medico), \"\") = \"%s\")\n",
                    p.getDni(), p.getDni()));
        }

        // CONTAINS solo cuando es el ÚNICO criterio de búsqueda significativo.
        // Si ya hay filtros de DNI, estado, especialidad o fechas, el nombre
        // residual es basura del parsing y causa Fuseki 500.
        boolean hayOtrosFiltros = p.getDni() != null || p.getEstado() != null
                || p.getEspecialidad() != null || p.getFechaInicio() != null;
        if (p.getNombreBusqueda() != null && !hayOtrosFiltros) {
            String n = p.getNombreBusqueda().toLowerCase();
            sb.append(String.format("  FILTER(CONTAINS(LCASE(?paciente), \"%s\") || CONTAINS(LCASE(?medico), \"%s\"))\n",
                    n, n));
        }

        if (p.getEspecialidad() != null) {
            sb.append(String.format("  FILTER(LCASE(STR(?especialidad)) = \"%s\")\n",
                    p.getEspecialidad().toLowerCase()));
        }
        if (p.getEstado() != null) {
            sb.append(String.format("  FILTER(LCASE(STR(?estado)) = \"%s\")\n",
                    p.getEstado().toLowerCase()));
        }

        appendDateFilters(sb, p);

        sb.append("}\nORDER BY DESC(?fecha)\nLIMIT 50\n");
        return sb.toString();
    }

    // ──────────── Helper de fechas ────────────

    private void appendDateFilters(StringBuilder sb, ParseResult p) {
        if (p.getFechaInicio() != null && p.getFechaFin() != null) {
            // Rango: mes pasado, semana pasada, fechas explícitas
            sb.append(String.format(
                    "  FILTER(xsd:date(STR(?fecha)) >= \"%s\"^^xsd:date && xsd:date(STR(?fecha)) <= \"%s\"^^xsd:date)\n",
                    p.getFechaInicio(), p.getFechaFin()
            ));
        } else if (p.getFechaInicio() != null) {
            String fi = p.getFechaInicio();
            if (fi.length() == 4) {
                // Año solo: "citas del 2023"
                sb.append(String.format("  FILTER(YEAR(xsd:date(STR(?fecha))) = %s)\n", fi));
            } else {
                // Fecha exacta: "hoy", "ayer", "2026-03-15"
                sb.append(String.format(
                        "  FILTER(xsd:date(STR(?fecha)) = \"%s\"^^xsd:date)\n", fi
                ));
            }
        }
    }

    private String buildFechaFilter(String varFecha, ParseResult p) {
        if (p.getFechaInicio() != null && p.getFechaFin() != null) {
            return String.format(
                    "    FILTER(xsd:date(STR(%s)) >= \"%s\"^^xsd:date && xsd:date(STR(%s)) <= \"%s\"^^xsd:date)\n",
                    varFecha, p.getFechaInicio(), varFecha, p.getFechaFin()
            );
        }
        String fi = p.getFechaInicio();
        if (fi != null && fi.length() == 4) {
            return String.format("    FILTER(YEAR(xsd:date(STR(%s))) = %s)\n", varFecha, fi);
        }
        if (fi != null) {
            return String.format("    FILTER(xsd:date(STR(%s)) = \"%s\"^^xsd:date)\n", varFecha, fi);
        }
        return "";
    }
}