package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.implementation;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryRequest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.NaturalLanguageQueryResponse;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.NaturalLanguageQueryService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SparqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class NaturalLanguageQueryServiceImpl implements NaturalLanguageQueryService {

    private static final String ONTO_PREFIX = "PREFIX onto: <http://nova.ing/ontology/>\n\n";

    private enum Intent {
        PACIENTES_CON_DIAGNOSTICO_DEFINITIVO,
        MEDICOS_POR_ESPECIALIDAD,
        MEDICOS_POR_DIA,
        PACIENTES_ACTIVOS_CON_CITAS_PROGRAMADAS,
        CITA_POR_ID,
        CITAS_POR_MEDICO,
        CITAS_POR_PACIENTE,
        CITAS_POR_NOMBRE,
        MEDICO_POR_ID,
        MEDICO_POR_NOMBRE,
        PACIENTE_POR_ID,
        PACIENTE_POR_NOMBRE,
        LISTAR_MEDICOS,
        LISTAR_MEDICOS_ACTIVOS,
        LISTAR_PACIENTES,
        LISTAR_PACIENTES_ACTIVOS,
        LISTAR_CITAS,
        LISTAR_DIAGNOSTICOS,
        DIAGNOSTICOS_POR_PACIENTE,
        DIAGNOSTICOS_POR_CITA
    }

    private static class IntentMatch {
        private final Intent intent;
        private final String id;
        private final String especialidadCodigo;
        private final String diaCodigo;
        private final String nombreTexto;

        private IntentMatch(Intent intent, String id, String especialidadCodigo, String diaCodigo, String nombreTexto) {
            this.intent = intent;
            this.id = id;
            this.especialidadCodigo = especialidadCodigo;
            this.diaCodigo = diaCodigo;
            this.nombreTexto = nombreTexto;
        }

        private static IntentMatch of(Intent intent) {
            return new IntentMatch(intent, null, null, null, null);
        }

        private IntentMatch withId(String id) {
            return new IntentMatch(this.intent, id, this.especialidadCodigo, this.diaCodigo, this.nombreTexto);
        }

        private IntentMatch withEspecialidadCodigo(String especialidadCodigo) {
            return new IntentMatch(this.intent, this.id, especialidadCodigo, this.diaCodigo, this.nombreTexto);
        }

        private IntentMatch withDiaCodigo(String diaCodigo) {
            return new IntentMatch(this.intent, this.id, this.especialidadCodigo, diaCodigo, this.nombreTexto);
        }

        private IntentMatch withNombreTexto(String nombreTexto) {
            return new IntentMatch(this.intent, this.id, this.especialidadCodigo, this.diaCodigo, nombreTexto);
        }
    }

    @Autowired
    private SparqlQueryService sparqlQueryService;

    @Override
    public NaturalLanguageQueryResponse ejecutarConsulta(NaturalLanguageQueryRequest request) {
        NaturalLanguageQueryResponse response = new NaturalLanguageQueryResponse();
        if (request == null || request.getPregunta() == null) {
            response.setMensaje("La pregunta no puede ser nula.");
            response.setResultados(Collections.emptyList());
            return response;
        }

        String preguntaOriginal = request.getPregunta();
        String preguntaNormalizada = normalizar(preguntaOriginal);

        String sparql = construirSparqlDesdePregunta(preguntaNormalizada);
        response.setPregunta(preguntaOriginal);

        if (sparql == null) {
            response.setMensaje("No se pudo interpretar la pregunta en términos de la ontología actual.");
            response.setResultados(Collections.emptyList());
            return response;
        }

        response.setSparql(sparql);
        List<Map<String, String>> resultados = sparqlQueryService.ejecutarSelectSistemaCompleto(sparql);
        response.setResultados(resultados);
        response.setMensaje("Consulta ejecutada correctamente.");
        return response;
    }

    private String normalizar(String texto) {
        String t = texto.trim().toLowerCase(Locale.ROOT);
        t = t.replace("á", "a")
             .replace("é", "e")
             .replace("í", "i")
             .replace("ó", "o")
             .replace("ú", "u")
             .replace("¿", "")
             .replace("?", "")
             .replace(",", " ");
        return t;
    }

    private String construirSparqlDesdePregunta(String pregunta) {
        IntentMatch match = detectarIntent(pregunta);
        if (match == null) {
            return null;
        }
        switch (match.intent) {
            case PACIENTES_CON_DIAGNOSTICO_DEFINITIVO:
                return consultaPacientesConDiagnosticoDefinitivo();
            case MEDICOS_POR_ESPECIALIDAD:
                if (match.especialidadCodigo == null) {
                    return null;
                }
                return consultaMedicosPorEspecialidad(match.especialidadCodigo);
            case MEDICOS_POR_DIA:
                if (match.diaCodigo == null) {
                    return null;
                }
                return consultaMedicosPorDiaSemana(match.diaCodigo);
            case PACIENTES_ACTIVOS_CON_CITAS_PROGRAMADAS:
                return consultaPacientesActivosConCitasProgramadas();
            case CITA_POR_ID:
                if (match.id == null) {
                    return null;
                }
                return consultaCitaPorId(match.id);
            case CITAS_POR_MEDICO:
                if (match.id == null) {
                    return null;
                }
                return consultaCitasPorMedico(match.id);
            case CITAS_POR_PACIENTE:
                if (match.id == null) {
                    return null;
                }
                return consultaCitasPorPaciente(match.id);
            case CITAS_POR_NOMBRE:
                if (match.nombreTexto == null) {
                    return null;
                }
                return consultaCitasPorNombre(match.nombreTexto);
            case MEDICO_POR_ID:
                if (match.id == null) {
                    return null;
                }
                return consultaMedicoPorId(match.id);
            case MEDICO_POR_NOMBRE:
                if (match.nombreTexto == null) {
                    return null;
                }
                return consultaMedicoPorNombre(match.nombreTexto);
            case PACIENTE_POR_ID:
                if (match.id == null) {
                    return null;
                }
                return consultaPacientePorId(match.id);
            case PACIENTE_POR_NOMBRE:
                if (match.nombreTexto == null) {
                    return null;
                }
                return consultaPacientePorNombre(match.nombreTexto);
            case LISTAR_MEDICOS:
                return consultaListarMedicos();
            case LISTAR_MEDICOS_ACTIVOS:
                return consultaMedicosActivos();
            case LISTAR_PACIENTES:
                return consultaListarPacientes();
            case LISTAR_PACIENTES_ACTIVOS:
                return consultaPacientesActivos();
            case LISTAR_CITAS:
                return consultaListarCitas();
            case LISTAR_DIAGNOSTICOS:
                return consultaListarDiagnosticos();
            case DIAGNOSTICOS_POR_PACIENTE:
                if (match.id == null) {
                    return null;
                }
                return consultaDiagnosticosPorPaciente(match.id);
            case DIAGNOSTICOS_POR_CITA:
                if (match.id == null) {
                    return null;
                }
                return consultaDiagnosticosPorCita(match.id);
            default:
                return null;
        }
    }

    private IntentMatch detectarIntent(String pregunta) {
        if (pregunta.contains("paciente") && pregunta.contains("diagnostico definitivo")) {
            return IntentMatch.of(Intent.PACIENTES_CON_DIAGNOSTICO_DEFINITIVO);
        }

        if (pregunta.contains("medico") && pregunta.contains("especialidad")) {
            String especialidad = extraerEspecialidad(pregunta);
            String codigo = mapearEspecialidad(especialidad);
            if (codigo == null) {
                return null;
            }
            return IntentMatch.of(Intent.MEDICOS_POR_ESPECIALIDAD).withEspecialidadCodigo(codigo);
        }

        if (pregunta.contains("medico") && contieneDiaSemana(pregunta)) {
            String dia = extraerDiaSemana(pregunta);
            if (dia == null) {
                return null;
            }
            return IntentMatch.of(Intent.MEDICOS_POR_DIA).withDiaCodigo(dia);
        }

        if (pregunta.contains("paciente") && pregunta.contains("cita") && pregunta.contains("activos")) {
            return IntentMatch.of(Intent.PACIENTES_ACTIVOS_CON_CITAS_PROGRAMADAS);
        }

        if (contieneVerboListar(pregunta)) {
            if (pregunta.contains("diagnostico")) {
                String id = extraerPrimerNumero(pregunta);
                if (pregunta.contains("paciente") && id != null) {
                    return IntentMatch.of(Intent.DIAGNOSTICOS_POR_PACIENTE).withId(id);
                }
                if (pregunta.contains("cita") && id != null) {
                    return IntentMatch.of(Intent.DIAGNOSTICOS_POR_CITA).withId(id);
                }
                return IntentMatch.of(Intent.LISTAR_DIAGNOSTICOS);
            }

            if (pregunta.contains("cita")) {
                String id = extraerPrimerNumero(pregunta);
                if (pregunta.contains("id") && id != null) {
                    return IntentMatch.of(Intent.CITA_POR_ID).withId(id);
                }
                if ((pregunta.contains("medico") || pregunta.contains("doctor")) && id != null) {
                    return IntentMatch.of(Intent.CITAS_POR_MEDICO).withId(id);
                }
                if (pregunta.contains("paciente") && id != null) {
                    return IntentMatch.of(Intent.CITAS_POR_PACIENTE).withId(id);
                }
                String nombre = extraerNombreLibre(pregunta);
                if (nombre != null) {
                    return IntentMatch.of(Intent.CITAS_POR_NOMBRE).withNombreTexto(nombre);
                }
                return IntentMatch.of(Intent.LISTAR_CITAS);
            }

            if (pregunta.contains("medico")) {
                String idMedico = extraerPrimerNumero(pregunta);
                if (idMedico != null) {
                    return IntentMatch.of(Intent.MEDICO_POR_ID).withId(idMedico);
                }
                String nombreMedico = extraerNombreLibre(pregunta);
                if (nombreMedico != null) {
                    return IntentMatch.of(Intent.MEDICO_POR_NOMBRE).withNombreTexto(nombreMedico);
                }
                if (pregunta.contains("activo") || pregunta.contains("activos")) {
                    return IntentMatch.of(Intent.LISTAR_MEDICOS_ACTIVOS);
                }
                return IntentMatch.of(Intent.LISTAR_MEDICOS);
            }

            if (pregunta.contains("paciente")) {
                String idPaciente = extraerPrimerNumero(pregunta);
                if (idPaciente != null) {
                    return IntentMatch.of(Intent.PACIENTE_POR_ID).withId(idPaciente);
                }
                String nombrePaciente = extraerNombreLibre(pregunta);
                if (nombrePaciente != null) {
                    return IntentMatch.of(Intent.PACIENTE_POR_NOMBRE).withNombreTexto(nombrePaciente);
                }
                if (pregunta.contains("activo") || pregunta.contains("activos")) {
                    return IntentMatch.of(Intent.LISTAR_PACIENTES_ACTIVOS);
                }
                return IntentMatch.of(Intent.LISTAR_PACIENTES);
            }
        }

        return null;
    }

    private String consultaPacientesConDiagnosticoDefinitivo() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?paciente ?nombre ?apellido\n" +
                "WHERE {\n" +
                "  ?paciente a onto:Paciente ;\n" +
                "            onto:nombres ?nombre ;\n" +
                "            onto:apellidos ?apellido .\n" +
                "  ?diag a onto:Diagnostico ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:tipoDiagnostico onto:TipoDiagnostico_DEFINITIVO .\n" +
                "}\n";
    }

    private String consultaMedicosPorEspecialidad(String codigoEspecialidad) {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?medico ?nombre ?apellido\n" +
                "WHERE {\n" +
                "  ?medico a onto:Medico ;\n" +
                "          onto:nombres ?nombre ;\n" +
                "          onto:apellidos ?apellido ;\n" +
                "          onto:especialidad onto:EspecialidadMedico_" + codigoEspecialidad + " .\n" +
                "}\n";
    }

    private String consultaMedicosPorDiaSemana(String diaCodigo) {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?medico ?nombre ?dia ?inicio ?fin\n" +
                "WHERE {\n" +
                "  ?medico a onto:Medico ;\n" +
                "          onto:nombres ?nombre ;\n" +
                "          onto:tieneHorario ?h .\n" +
                "  ?h a onto:Horario ;\n" +
                "     onto:diaSemana ?dia ;\n" +
                "     onto:horaInicio ?inicio ;\n" +
                "     onto:horaFin ?fin .\n" +
                "  FILTER(?dia = \"" + diaCodigo + "\")\n" +
                "}\n";
    }

    private String consultaPacientesActivosConCitasProgramadas() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?paciente ?nombre ?apellido\n" +
                "WHERE {\n" +
                "  ?paciente a onto:Paciente ;\n" +
                "            onto:nombres ?nombre ;\n" +
                "            onto:apellidos ?apellido ;\n" +
                "            onto:estado onto:EstadoPaciente_ACTIVO .\n" +
                "  ?cita a onto:Cita ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:estado onto:EstadoCita_PROGRAMADA .\n" +
                "}\n";
    }

    private String consultaListarMedicos() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?medico ?nombre ?apellido ?especialidad ?estado\n" +
                "WHERE {\n" +
                "  ?medico a onto:Medico ;\n" +
                "          onto:nombres ?nombre ;\n" +
                "          onto:apellidos ?apellido .\n" +
                "  OPTIONAL { ?medico onto:especialidad ?especialidad . }\n" +
                "  OPTIONAL { ?medico onto:estado ?estado . }\n" +
                "}\n";
    }

    private String consultaMedicosActivos() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?medico ?nombre ?apellido ?estado\n" +
                "WHERE {\n" +
                "  ?medico a onto:Medico ;\n" +
                "          onto:nombres ?nombre ;\n" +
                "          onto:apellidos ?apellido ;\n" +
                "          onto:estado ?estado .\n" +
                "  FILTER(?estado = onto:EstadoMedico_ACTIVO)\n" +
                "}\n";
    }

    private String consultaListarPacientes() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?paciente ?nombre ?apellido ?estado\n" +
                "WHERE {\n" +
                "  ?paciente a onto:Paciente ;\n" +
                "            onto:nombres ?nombre ;\n" +
                "            onto:apellidos ?apellido .\n" +
                "  OPTIONAL { ?paciente onto:estado ?estado . }\n" +
                "}\n";
    }

    private String consultaMedicoPorId(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/medico/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?medico ?nombre ?apellido ?estado ?especialidad\n" +
                "WHERE {\n" +
                "  ?medico a onto:Medico ;\n" +
                "          onto:nombres ?nombre ;\n" +
                "          onto:apellidos ?apellido .\n" +
                "  OPTIONAL { ?medico onto:estado ?estado . }\n" +
                "  OPTIONAL { ?medico onto:especialidad ?especialidad . }\n" +
                "  FILTER(STR(?medico) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String consultaMedicoPorNombre(String nombreTexto) {
        String nombreFiltro = nombreTexto.toLowerCase(Locale.ROOT);
        return ONTO_PREFIX +
                "SELECT DISTINCT ?medico ?nombre ?apellido ?estado ?especialidad\n" +
                "WHERE {\n" +
                "  ?medico a onto:Medico ;\n" +
                "          onto:nombres ?nombre ;\n" +
                "          onto:apellidos ?apellido .\n" +
                "  OPTIONAL { ?medico onto:estado ?estado . }\n" +
                "  OPTIONAL { ?medico onto:especialidad ?especialidad . }\n" +
                "  FILTER(CONTAINS(LCASE(CONCAT(?nombre, \" \", ?apellido)), \"" + nombreFiltro + "\"))\n" +
                "}\n";
    }

    private String consultaPacientePorId(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/paciente/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?paciente ?nombre ?apellido ?estado\n" +
                "WHERE {\n" +
                "  ?paciente a onto:Paciente ;\n" +
                "            onto:nombres ?nombre ;\n" +
                "            onto:apellidos ?apellido .\n" +
                "  OPTIONAL { ?paciente onto:estado ?estado . }\n" +
                "  FILTER(STR(?paciente) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String consultaPacientePorNombre(String nombreTexto) {
        String nombreFiltro = nombreTexto.toLowerCase(Locale.ROOT);
        return ONTO_PREFIX +
                "SELECT DISTINCT ?paciente ?nombre ?apellido ?estado\n" +
                "WHERE {\n" +
                "  ?paciente a onto:Paciente ;\n" +
                "            onto:nombres ?nombre ;\n" +
                "            onto:apellidos ?apellido .\n" +
                "  OPTIONAL { ?paciente onto:estado ?estado . }\n" +
                "  FILTER(CONTAINS(LCASE(CONCAT(?nombre, \" \", ?apellido)), \"" + nombreFiltro + "\"))\n" +
                "}\n";
    }

    private String consultaPacientesActivos() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?paciente ?nombre ?apellido\n" +
                "WHERE {\n" +
                "  ?paciente a onto:Paciente ;\n" +
                "            onto:nombres ?nombre ;\n" +
                "            onto:apellidos ?apellido ;\n" +
                "            onto:estado onto:EstadoPaciente_ACTIVO .\n" +
                "}\n";
    }

    private String consultaListarCitas() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?cita ?fecha ?horaInicio ?horaFin ?paciente ?medico ?estado ?pacienteNombre ?pacienteApellido ?pacienteEstado ?medicoNombre ?medicoApellido ?medicoEstado\n" +
                "WHERE {\n" +
                "  ?cita a onto:Cita ;\n" +
                "        onto:fechaCita ?fecha ;\n" +
                "        onto:horaInicio ?horaInicio ;\n" +
                "        onto:horaFin ?horaFin ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:medico ?medico .\n" +
                "  ?paciente onto:nombres ?pacienteNombre ;\n" +
                "            onto:apellidos ?pacienteApellido .\n" +
                "  ?medico onto:nombres ?medicoNombre ;\n" +
                "          onto:apellidos ?medicoApellido .\n" +
                "  OPTIONAL { ?cita onto:estado ?estado . }\n" +
                "  OPTIONAL { ?paciente onto:estado ?pacienteEstado . }\n" +
                "  OPTIONAL { ?medico   onto:estado ?medicoEstado . }\n" +
                "}\n";
    }

    private String consultaCitaPorId(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/cita/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?cita ?fecha ?horaInicio ?horaFin ?paciente ?medico ?estado ?pacienteNombre ?pacienteApellido ?pacienteEstado ?medicoNombre ?medicoApellido ?medicoEstado\n" +
                "WHERE {\n" +
                "  ?cita a onto:Cita ;\n" +
                "        onto:fechaCita ?fecha ;\n" +
                "        onto:horaInicio ?horaInicio ;\n" +
                "        onto:horaFin ?horaFin ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:medico ?medico .\n" +
                "  ?paciente onto:nombres ?pacienteNombre ;\n" +
                "            onto:apellidos ?pacienteApellido .\n" +
                "  ?medico onto:nombres ?medicoNombre ;\n" +
                "          onto:apellidos ?medicoApellido .\n" +
                "  OPTIONAL { ?cita onto:estado ?estado . }\n" +
                "  OPTIONAL { ?paciente onto:estado ?pacienteEstado . }\n" +
                "  OPTIONAL { ?medico   onto:estado ?medicoEstado . }\n" +
                "  FILTER(STR(?cita) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String consultaCitasPorMedico(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/medico/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?cita ?fecha ?horaInicio ?horaFin ?paciente ?medico ?estado ?pacienteNombre ?pacienteApellido ?pacienteEstado ?medicoNombre ?medicoApellido ?medicoEstado\n" +
                "WHERE {\n" +
                "  ?cita a onto:Cita ;\n" +
                "        onto:fechaCita ?fecha ;\n" +
                "        onto:horaInicio ?horaInicio ;\n" +
                "        onto:horaFin ?horaFin ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:medico ?medico .\n" +
                "  ?paciente onto:nombres ?pacienteNombre ;\n" +
                "            onto:apellidos ?pacienteApellido .\n" +
                "  ?medico onto:nombres ?medicoNombre ;\n" +
                "          onto:apellidos ?medicoApellido .\n" +
                "  OPTIONAL { ?cita onto:estado ?estado . }\n" +
                "  OPTIONAL { ?paciente onto:estado ?pacienteEstado . }\n" +
                "  OPTIONAL { ?medico   onto:estado ?medicoEstado . }\n" +
                "  FILTER(STR(?medico) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String consultaCitasPorPaciente(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/paciente/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?cita ?fecha ?horaInicio ?horaFin ?paciente ?medico ?estado ?pacienteNombre ?pacienteApellido ?pacienteEstado ?medicoNombre ?medicoApellido ?medicoEstado\n" +
                "WHERE {\n" +
                "  ?cita a onto:Cita ;\n" +
                "        onto:fechaCita ?fecha ;\n" +
                "        onto:horaInicio ?horaInicio ;\n" +
                "        onto:horaFin ?horaFin ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:medico ?medico .\n" +
                "  ?paciente onto:nombres ?pacienteNombre ;\n" +
                "            onto:apellidos ?pacienteApellido .\n" +
                "  ?medico onto:nombres ?medicoNombre ;\n" +
                "          onto:apellidos ?medicoApellido .\n" +
                "  OPTIONAL { ?cita onto:estado ?estado . }\n" +
                "  OPTIONAL { ?paciente onto:estado ?pacienteEstado . }\n" +
                "  OPTIONAL { ?medico   onto:estado ?medicoEstado . }\n" +
                "  FILTER(STR(?paciente) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String consultaCitasPorNombre(String nombreTexto) {
        String nombre = nombreTexto.toLowerCase(Locale.ROOT);
        return ONTO_PREFIX +
                "SELECT DISTINCT ?cita ?fecha ?horaInicio ?horaFin ?paciente ?medico ?estado ?pacienteNombre ?pacienteApellido ?pacienteEstado ?medicoNombre ?medicoApellido ?medicoEstado\n" +
                "WHERE {\n" +
                "  ?cita a onto:Cita ;\n" +
                "        onto:fechaCita ?fecha ;\n" +
                "        onto:horaInicio ?horaInicio ;\n" +
                "        onto:horaFin ?horaFin ;\n" +
                "        onto:paciente ?paciente ;\n" +
                "        onto:medico ?medico .\n" +
                "  OPTIONAL { ?cita onto:estado ?estado . }\n" +
                "  ?paciente onto:nombres ?pacienteNombre ;\n" +
                "            onto:apellidos ?pacienteApellido .\n" +
                "  OPTIONAL { ?paciente onto:estado ?pacienteEstado . }\n" +
                "  ?medico onto:nombres ?medicoNombre ;\n" +
                "          onto:apellidos ?medicoApellido .\n" +
                "  OPTIONAL { ?medico onto:estado ?medicoEstado . }\n" +
                "  FILTER(\n" +
                "    CONTAINS(LCASE(CONCAT(?pacienteNombre, \" \", ?pacienteApellido)), \"" + nombre + "\") ||\n" +
                "    CONTAINS(LCASE(CONCAT(?medicoNombre, \" \", ?medicoApellido)), \"" + nombre + "\")\n" +
                "  )\n" +
                "}\n";
    }

    private String consultaListarDiagnosticos() {
        return ONTO_PREFIX +
                "SELECT DISTINCT ?diagnostico ?descripcion ?tipo ?fecha ?paciente ?cita\n" +
                "WHERE {\n" +
                "  ?diagnostico a onto:Diagnostico ;\n" +
                "               onto:descripcion ?descripcion ;\n" +
                "               onto:paciente ?paciente ;\n" +
                "               onto:cita ?cita .\n" +
                "  OPTIONAL { ?diagnostico onto:tipoDiagnostico ?tipo . }\n" +
                "  OPTIONAL { ?diagnostico onto:fechaDiagnostico ?fecha . }\n" +
                "}\n";
    }

    private String consultaDiagnosticosPorPaciente(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/paciente/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?diagnostico ?descripcion ?tipo ?fecha ?paciente ?cita\n" +
                "WHERE {\n" +
                "  ?diagnostico a onto:Diagnostico ;\n" +
                "               onto:descripcion ?descripcion ;\n" +
                "               onto:paciente ?paciente ;\n" +
                "               onto:cita ?cita .\n" +
                "  OPTIONAL { ?diagnostico onto:tipoDiagnostico ?tipo . }\n" +
                "  OPTIONAL { ?diagnostico onto:fechaDiagnostico ?fecha . }\n" +
                "  FILTER(STR(?paciente) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String consultaDiagnosticosPorCita(String idTexto) {
        String iri = "http://nova.ing/atencion-medica/cita/" + idTexto;
        return ONTO_PREFIX +
                "SELECT DISTINCT ?diagnostico ?descripcion ?tipo ?fecha ?paciente ?cita\n" +
                "WHERE {\n" +
                "  ?diagnostico a onto:Diagnostico ;\n" +
                "               onto:descripcion ?descripcion ;\n" +
                "               onto:paciente ?paciente ;\n" +
                "               onto:cita ?cita .\n" +
                "  OPTIONAL { ?diagnostico onto:tipoDiagnostico ?tipo . }\n" +
                "  OPTIONAL { ?diagnostico onto:fechaDiagnostico ?fecha . }\n" +
                "  FILTER(STR(?cita) = \"" + iri + "\")\n" +
                "}\n";
    }

    private String extraerPrimerNumero(String pregunta) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pregunta.length(); i++) {
            char c = pregunta.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            } else if (sb.length() > 0) {
                break;
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    private boolean contieneVerboListar(String pregunta) {
        return pregunta.contains("lista") ||
                pregunta.contains("listar") ||
                pregunta.contains("listame") ||
                pregunta.contains("muestrame") ||
                pregunta.contains("mostrar") ||
                pregunta.contains("obten") ||
                pregunta.contains("obtener") ||
                pregunta.contains("dame");
    }

    private String extraerEspecialidad(String pregunta) {
        int idx = pregunta.indexOf("especialidad");
        if (idx == -1) {
            return null;
        }
        String resto = pregunta.substring(idx);
        String[] tokens = resto.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (token.equals("especialidad") || token.equals("de") || token.equals("en")) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(token);
        }
        return sb.toString().trim();
    }

    private String mapearEspecialidad(String texto) {
        if (texto == null || texto.isEmpty()) {
            return null;
        }
        String t = texto.toLowerCase(Locale.ROOT);
        if (t.contains("medicina general")) {
            return "MEDICINA_GENERAL";
        }
        if (t.contains("pediatria")) {
            return "PEDIATRIA";
        }
        if (t.contains("cardiologia")) {
            return "CARDIOLOGIA";
        }
        if (t.contains("dermatologia")) {
            return "DERMATOLOGIA";
        }
        return null;
    }

    private boolean contieneDiaSemana(String pregunta) {
        return pregunta.contains("lunes") ||
                pregunta.contains("martes") ||
                pregunta.contains("miercoles") ||
                pregunta.contains("jueves") ||
                pregunta.contains("viernes") ||
                pregunta.contains("sabado") ||
                pregunta.contains("domingo");
    }

    private String extraerDiaSemana(String pregunta) {
        if (pregunta.contains("lunes")) {
            return "MONDAY";
        }
        if (pregunta.contains("martes")) {
            return "TUESDAY";
        }
        if (pregunta.contains("miercoles")) {
            return "WEDNESDAY";
        }
        if (pregunta.contains("jueves")) {
            return "THURSDAY";
        }
        if (pregunta.contains("viernes")) {
            return "FRIDAY";
        }
        if (pregunta.contains("sabado")) {
            return "SATURDAY";
        }
        if (pregunta.contains("domingo")) {
            return "SUNDAY";
        }
        return null;
    }

    private String extraerNombreLibre(String pregunta) {
        int idx = -1;
        int offset = 0;
        int tmp = pregunta.lastIndexOf(" del ");
        if (tmp != -1) {
            idx = tmp;
            offset = 5;
        }
        tmp = pregunta.lastIndexOf(" de ");
        if (tmp > idx) {
            idx = tmp;
            offset = 4;
        }
        if (idx == -1) {
            return null;
        }
        String nombre = pregunta.substring(idx + offset).trim();
        if (nombre.isEmpty()) {
            return null;
        }
        return nombre;
    }
}
