package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.DiagnosticoSyncDTO;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.SyncRequestDTO;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers.ParseResult;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers.QueryParser;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers.SparqlBuilder;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.repositories.FusekiRepository;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.vocabulary.MED;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SemanticServiceImpl implements ISemanticService {

    private final FusekiRepository fusekiRepository;
    private final QueryParser queryParser;
    private final SparqlBuilder sparqlBuilder;

    @Override
    public List<Map<String, String>> buscarEnLenguajeNatural(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            log.warn("Se recibió una búsqueda vacía.");
            return List.of();
        }

        log.info("Procesando búsqueda semántica para: '{}'", texto);

        try {
            // 1. El Parser extrae intenciones, filtros, nombres, rangos de fecha y horas
            ParseResult parseResult = queryParser.parse(texto);
            
            if (parseResult.sinFiltros()) {
                log.info("No se identificaron filtros específicos en la consulta. Se realizará una búsqueda general.");
            } else {
                log.info("Filtros detectados: DNI={}, Especialidad={}, Estado={}, FechaInicio={}, FechaFin={}", 
                        parseResult.getDni(), parseResult.getEspecialidad(), parseResult.getEstado(), 
                        parseResult.getFechaInicio(), parseResult.getFechaFin());
            }

            // 2. El Builder construye el Query SPARQL (Listado, Ranking o Disponibilidad)
            String query = sparqlBuilder.buildSearchQuery(parseResult);
            log.debug("SPARQL generado:\n{}", query);

            // 3. El Repositorio ejecuta la consulta en el servidor Fuseki
            List<Map<String, String>> resultados = fusekiRepository.executeSelect(query);
            
            if (resultados.isEmpty()) {
                String mensaje = generarMensajeSinResultados(parseResult, texto);
                log.info("Búsqueda completada sin resultados. Mensaje: {}", mensaje);
                // Lanzamos una excepción controlada para que el frontend reciba el mensaje amigable
                throw new org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions.SemanticException(mensaje);
            }

            log.info("Búsqueda completada. Resultados encontrados: {}", resultados.size());
            return resultados;

        } catch (org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions.SemanticException e) {
            // Manejar FusekiConnectionException de forma especial si es necesario
            if (e instanceof org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions.FusekiConnectionException) {
                log.error("Error de conexión con Fuseki: {}", e.getMessage());
                throw new org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions.SemanticException(
                    "Lo siento, no pude conectarme al motor de búsqueda en este momento. Por favor, intenta de nuevo más tarde."
                );
            }
            // Re-lanzar excepciones semánticas ya formateadas (incluyendo el mensaje de "No se encontraron...")
            throw e;
        } catch (Exception e) {
            log.error("Error al procesar la búsqueda semántica: {}", e.getMessage(), e);
            throw new org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions.SemanticException(
                    "Ocurrió un error inesperado al procesar tu búsqueda. Por favor, intenta simplificar tu consulta."
            );
        }
    }

    private String generarMensajeSinResultados(ParseResult p, String textoOriginal) {
        if (p.getDni() != null) {
            return "No se encontraron citas para el DNI " + p.getDni();
        }
        
        if (p.getFechaInicio() != null && p.getFechaFin() != null) {
            return String.format("No se encontraron citas en el periodo del %s al %s", 
                formatearFecha(p.getFechaInicio()), formatearFecha(p.getFechaFin()));
        }
        
        if (p.getFechaInicio() != null) {
            String fechaStr = p.getFechaInicio();
            if (textoOriginal.toLowerCase().contains("hoy")) {
                return "No hay citas para hoy";
            }
            if (fechaStr.length() == 4) {
                return "No hay citas para el año " + fechaStr;
            }
            return "No hay citas programadas para el " + formatearFecha(fechaStr);
        }
        
        if (p.getEspecialidad() != null) {
            return "No se encontraron citas para la especialidad de " + p.getEspecialidad().toLowerCase();
        }
        
        return "No se encontraron resultados para tu búsqueda: \"" + textoOriginal + "\"";
    }

    private String formatearFecha(String isoDate) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(isoDate);
            return date.format(java.time.format.DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES")));
        } catch (Exception e) {
            return isoDate;
        }
    }

    @Override
    public void sincronizarAtencionMedica(SyncRequestDTO dto) {
        log.info("Sincronizando Cita ID: {} en el Grafo Semántico", dto.getCitaId());

        String citaUri    = "med:Cita_" + dto.getCitaId();
        String pacienteUri = "med:Paciente_" + dto.getPacienteId();
        String medicoUri   = "med:Medico_" + dto.getMedicoId();

        // ── 1. ELIMINAR tripletas previas de esta cita, paciente y médico ──
        StringBuilder delete = new StringBuilder();
        delete.append("PREFIX med: <").append(MED.NS).append(">\n");
        delete.append("DELETE WHERE { ").append(citaUri).append(" ?p ?o } ;\n");
        delete.append("DELETE WHERE { ").append(pacienteUri).append(" ?p ?o } ;\n");
        delete.append("DELETE WHERE { ").append(medicoUri).append(" ?p ?o }");

        fusekiRepository.executeUpdate(delete.toString());

        // ── 2. INSERTAR datos frescos ──
        StringBuilder insert = new StringBuilder();
        insert.append("PREFIX med: <").append(MED.NS).append(">\n");
        insert.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        insert.append("INSERT DATA {\n");

        // Cita
        insert.append(String.format("  %s rdf:type med:Cita ;\n", citaUri));
        insert.append(String.format("    med:fechaCita \"%s\" ;\n", dto.getFechaCita()));
        if (dto.getHoraInicio() != null) {
            insert.append(String.format("    med:horaInicio \"%s\" ;\n", dto.getHoraInicio()));
        }
        if (dto.getHoraFin() != null) {
            insert.append(String.format("    med:horaFin \"%s\" ;\n", dto.getHoraFin()));
        }
        insert.append(String.format("    med:motivoCita \"%s\" ;\n", dto.getMotivo()));
        insert.append(String.format("    med:estadoCita \"%s\" ;\n", dto.getEstadoCita()));
        insert.append(String.format("    med:citaAgendadaPara %s ;\n", pacienteUri));
        insert.append(String.format("    med:atendidaPor %s .\n", medicoUri));

        // Paciente
        insert.append(String.format("  %s rdf:type med:Paciente ;\n", pacienteUri));
        insert.append(String.format("    med:dniPaciente \"%s\" ;\n", dto.getDniPaciente()));
        insert.append(String.format("    med:nombreCompleto \"%s %s\" .\n", dto.getNombrePaciente(), dto.getApellidoPaciente()));

        // Médico
        insert.append(String.format("  %s rdf:type med:Medico ;\n", medicoUri));
        insert.append(String.format("    med:dniMedico \"%s\" ;\n", dto.getDniMedico()));
        insert.append(String.format("    med:nombreMedico \"%s\" ;\n", dto.getNombreMedico()));
        insert.append(String.format("    med:especialidad \"%s\" .\n", dto.getEspecialidad()));

        insert.append("}");

        fusekiRepository.executeUpdate(insert.toString());
        log.info("Cita {} sincronizada exitosamente.", dto.getCitaId());
    }

    @Override
    public void sincronizarDiagnostico(DiagnosticoSyncDTO dto) {
        log.info("Sincronizando Diagnóstico ID: {} para Cita ID: {}", dto.getId(), dto.getCitaId());

        String citaUri = "med:Cita_" + dto.getCitaId();
        String diagUri = "med:Diag_" + dto.getId();

        // 1. ELIMINAR tripletas previas del diagnóstico
        String delete = "PREFIX med: <" + MED.NS + ">\n" +
                "DELETE WHERE { " + diagUri + " ?p ?o } ;\n" +
                "DELETE WHERE { " + citaUri + " med:generaDiagnostico " + diagUri + " }";

        fusekiRepository.executeUpdate(delete);

        // 2. INSERTAR datos frescos
        String insert = "PREFIX med: <" + MED.NS + ">\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "INSERT DATA {\n" +
                "  " + diagUri + " rdf:type med:Diagnostico ;\n" +
                "    med:descripcionDiag \"" + dto.getDescripcion() + "\" ;\n" +
                "    med:tipoDiag \"" + dto.getTipoDiagnostico() + "\" .\n" +
                "  " + citaUri + " med:generaDiagnostico " + diagUri + " .\n" +
                "}";

        fusekiRepository.executeUpdate(insert);
    }

    @Override
    public List<Map<String, String>> consultarSparql(String query) {
        return fusekiRepository.executeSelect(query);
    }
}