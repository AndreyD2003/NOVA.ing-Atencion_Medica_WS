package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.implementation;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients.CitaClientRest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients.DiagnosticoClientRest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients.MedicoClientRest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients.PacienteClientRest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.CitaRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.DiagnosticoRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.MedicoRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.PacienteRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.HorarioRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.semantic.RdfModelBuilder;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.RdfGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RdfGraphServiceImpl implements RdfGraphService {

    private static final String ONTO = "http://nova.ing/ontology/";
    private static final String BASE_IRI = "http://nova.ing/atencion-medica/";

    @Autowired
    private RdfModelBuilder rdfModelBuilder;

    @Autowired
    private PacienteClientRest pacienteClientRest;

    @Autowired
    private MedicoClientRest medicoClientRest;

    @Autowired
    private CitaClientRest citaClientRest;

    @Autowired
    private DiagnosticoClientRest diagnosticoClientRest;

    @Override
    public void sincronizarDatosSistema() {
        Dataset dataset = rdfModelBuilder.getDataset();
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            
            // Opcional: Limpiar datos anteriores de instancias (manteniendo ontologia)
            // model.removeAll(); 
            // model.read("ontology/nova-ontology.owl"); // Recargar ontologia base
            
            // Para simplicidad, agregamos/actualizamos sobre lo existente.
            // En produccion, deberias gestionar actualizaciones incrementales.

            List<PacienteRemoteDto> pacientes = pacienteClientRest.listar();
            Map<Long, Resource> recursosPacientes = new HashMap<>();
            for (PacienteRemoteDto p : pacientes) {
                if (p == null || p.getId() == null) {
                    continue;
                }
                Resource r = crearRecursoPaciente(model, p);
                recursosPacientes.put(p.getId(), r);
            }

            List<MedicoRemoteDto> medicos = medicoClientRest.listar();
            Map<Long, Resource> recursosMedicos = new HashMap<>();
            for (MedicoRemoteDto m : medicos) {
                if (m == null || m.getId() == null) {
                    continue;
                }
                Resource r = crearRecursoMedico(model, m);
                recursosMedicos.put(m.getId(), r);
            }

            List<CitaRemoteDto> citas = citaClientRest.listarTodas();
            Map<Long, Resource> recursosCitas = new HashMap<>();
            for (CitaRemoteDto c : citas) {
                if (c == null || c.getId() == null) {
                    continue;
                }
                Resource paciente = c.getPacienteId() != null ? recursosPacientes.get(c.getPacienteId()) : null;
                Resource medico = c.getMedicoId() != null ? recursosMedicos.get(c.getMedicoId()) : null;
                Resource r = crearRecursoCita(model, c, paciente, medico);
                recursosCitas.put(c.getId(), r);
            }

            List<DiagnosticoRemoteDto> diagnosticos = diagnosticoClientRest.listar();
            for (DiagnosticoRemoteDto d : diagnosticos) {
                if (d == null || d.getId() == null) {
                    continue;
                }
                Resource cita = d.getCitaId() != null ? recursosCitas.get(d.getCitaId()) : null;
                Resource paciente = d.getPacienteId() != null ? recursosPacientes.get(d.getPacienteId()) : null;
                crearRecursoDiagnostico(model, d, cita, paciente);
            }
            
            dataset.commit();
        } catch (Exception e) {
            dataset.abort();
            throw e;
        } finally {
            dataset.end();
        }
    }

    @Override
    public Model obtenerModeloLectura() {
        Dataset dataset = rdfModelBuilder.getDataset();
        dataset.begin(ReadWrite.READ);
        try {
            Model model = dataset.getDefaultModel();
            // Retornamos el modelo con inferencia (solo lectura)
            return aplicarRazonamientoOwl(model);
        } finally {
            dataset.end();
        }
    }

    @Override
    public List<Map<String, String>> ejecutarConsultaSparql(String sparql) {
        Dataset dataset = rdfModelBuilder.getDataset();
        dataset.begin(ReadWrite.READ);
        try {
            Model model = dataset.getDefaultModel();
            // Aplicar inferencia
            Model inferido = aplicarRazonamientoOwl(model);
            
            try (QueryExecution qexec = QueryExecutionFactory.create(sparql, inferido)) {
                ResultSet results = qexec.execSelect();
                return ResultSetFormatter.toList(results).stream().map(sol -> {
                    Map<String, String> map = new HashMap<>();
                    sol.varNames().forEachRemaining(varName -> {
                        RDFNode node = sol.get(varName);
                        map.put(varName, node.toString());
                    });
                    return map;
                }).collect(java.util.stream.Collectors.toList());
            }
        } finally {
            dataset.end();
        }
    }

    @Override
    public String serializarModeloSistemaCompleto(String formato) {
        Model model = obtenerModeloLectura();
        return serializar(model, formato);
    }

    private String serializar(Model model, String formato) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Lang lang = mapFormato(formato);
        RDFDataMgr.write(out, model, lang);
        return out.toString();
    }

    private Lang mapFormato(String formato) {
        if (formato == null) {
            return Lang.TURTLE;
        }
        String f = formato.toUpperCase(Locale.ROOT);
        switch (f) {
            case "RDFXML":
            case "RDF/XML":
                return Lang.RDFXML;
            case "JSONLD":
            case "JSON-LD":
                return Lang.JSONLD;
            case "TTL":
            case "TURTLE":
            default:
                return Lang.TURTLE;
        }
    }

    private Model aplicarRazonamientoOwl(Model base) {
        Model esquema = ModelFactory.createDefaultModel();

        Resource clasePaciente = esquema.createResource(ONTO + "Paciente");
        Resource clasePacienteCronico = esquema.createResource(ONTO + "PacienteCronico");
        esquema.add(clasePacienteCronico, RDFS.subClassOf, clasePaciente);

        Resource claseMedico = esquema.createResource(ONTO + "Medico");
        Resource claseProfesionalSalud = esquema.createResource(ONTO + "ProfesionalSalud");
        esquema.add(claseMedico, RDFS.subClassOf, claseProfesionalSalud);

        Resource claseCita = esquema.createResource(ONTO + "Cita");
        Resource claseEventoClinico = esquema.createResource(ONTO + "EventoClinico");
        esquema.add(claseCita, RDFS.subClassOf, claseEventoClinico);

        Resource claseHorario = esquema.createResource(ONTO + "Horario");
        Resource claseEspecialidadMedico = esquema.createResource(ONTO + "EspecialidadMedico");
        Resource claseEstadoMedico = esquema.createResource(ONTO + "EstadoMedico");
        Resource claseEstadoPaciente = esquema.createResource(ONTO + "EstadoPaciente");
        Resource claseEstadoCita = esquema.createResource(ONTO + "EstadoCita");

        Resource propPaciente = esquema.createProperty(ONTO, "paciente");
        esquema.add(propPaciente, RDFS.domain, claseCita);
        esquema.add(propPaciente, RDFS.range, clasePaciente);

        Resource propMedico = esquema.createProperty(ONTO, "medico");
        esquema.add(propMedico, RDFS.domain, claseCita);
        esquema.add(propMedico, RDFS.range, claseMedico);

        Resource propCita = esquema.createProperty(ONTO, "cita");
        esquema.add(propCita, RDFS.domain, esquema.createResource(ONTO + "Diagnostico"));
        esquema.add(propCita, RDFS.range, claseCita);

        Resource propTieneHorario = esquema.createProperty(ONTO, "tieneHorario");
        esquema.add(propTieneHorario, RDFS.domain, claseMedico);
        esquema.add(propTieneHorario, RDFS.range, claseHorario);

        Resource propHorarioDe = esquema.createProperty(ONTO, "horarioDe");
        esquema.add(propHorarioDe, RDFS.domain, claseHorario);
        esquema.add(propHorarioDe, RDFS.range, claseMedico);

        Reasoner razonador = ReasonerRegistry.getOWLReasoner().bindSchema(esquema);
        InfModel modeloInferido = ModelFactory.createInfModel(razonador, base);
        return modeloInferido;
    }

    private Resource crearRecursoPaciente(Model model, PacienteRemoteDto p) {
        String iri = BASE_IRI + "paciente/" + p.getId();
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "Paciente"));
        if (p.getNombres() != null) {
            r.addLiteral(model.createProperty(ONTO, "nombres"), p.getNombres());
        }
        if (p.getApellidos() != null) {
            r.addLiteral(model.createProperty(ONTO, "apellidos"), p.getApellidos());
        }
        if (p.getFechaNacimiento() != null) {
            r.addLiteral(model.createProperty(ONTO, "fechaNacimiento"), p.getFechaNacimiento());
        }
        if (p.getGenero() != null) {
            r.addLiteral(model.createProperty(ONTO, "genero"), p.getGenero());
        }
        if (p.getDni() != null) {
            r.addLiteral(model.createProperty(ONTO, "dni"), p.getDni());
        }
        if (p.getTelefono() != null) {
            r.addLiteral(model.createProperty(ONTO, "telefono"), p.getTelefono());
        }
        if (p.getEmail() != null) {
            r.addLiteral(model.createProperty(ONTO, "email"), p.getEmail());
        }
        if (p.getDireccion() != null) {
            r.addLiteral(model.createProperty(ONTO, "direccion"), p.getDireccion());
        }
        if (p.getEstado() != null) {
            Resource estado = crearRecursoEstadoPaciente(model, p.getEstado());
            r.addProperty(model.createProperty(ONTO, "estado"), estado);
        }
        return r;
    }

    private Resource crearRecursoMedico(Model model, MedicoRemoteDto m) {
        String iri = BASE_IRI + "medico/" + m.getId();
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "Medico"));
        if (m.getNombres() != null) {
            r.addLiteral(model.createProperty(ONTO, "nombres"), m.getNombres());
        }
        if (m.getApellidos() != null) {
            r.addLiteral(model.createProperty(ONTO, "apellidos"), m.getApellidos());
        }
        if (m.getEspecialidad() != null) {
            Resource esp = crearRecursoEspecialidadMedico(model, m.getEspecialidad());
            r.addProperty(model.createProperty(ONTO, "especialidad"), esp);
        }
        if (m.getTelefono() != null) {
            r.addLiteral(model.createProperty(ONTO, "telefono"), m.getTelefono());
        }
        if (m.getEmail() != null) {
            r.addLiteral(model.createProperty(ONTO, "email"), m.getEmail());
        }
        if (m.getDni() != null) {
            r.addLiteral(model.createProperty(ONTO, "dni"), m.getDni());
        }
        if (m.getEstado() != null) {
            Resource estado = crearRecursoEstadoMedico(model, m.getEstado());
            r.addProperty(model.createProperty(ONTO, "estado"), estado);
        }

        if (m.getHorarios() != null) {
            for (HorarioRemoteDto h : m.getHorarios()) {
                if (h == null || h.getId() == null) {
                    continue;
                }
                Resource rh = crearRecursoHorario(model, h, r);
                r.addProperty(model.createProperty(ONTO, "tieneHorario"), rh);
            }
        }
        return r;
    }

    private Resource crearRecursoCita(Model model, CitaRemoteDto c, Resource paciente, Resource medico) {
        String iri = BASE_IRI + "cita/" + c.getId();
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "Cita"));
        if (c.getFechaCita() != null) {
            r.addLiteral(model.createProperty(ONTO, "fechaCita"), c.getFechaCita());
        }
        if (c.getHoraInicio() != null) {
            r.addLiteral(model.createProperty(ONTO, "horaInicio"), c.getHoraInicio());
        }
        if (c.getHoraFin() != null) {
            r.addLiteral(model.createProperty(ONTO, "horaFin"), c.getHoraFin());
        }
        if (c.getMotivo() != null) {
            r.addLiteral(model.createProperty(ONTO, "motivo"), c.getMotivo());
        }
        if (c.getEstado() != null) {
            Resource estado = crearRecursoEstadoCita(model, c.getEstado());
            r.addProperty(model.createProperty(ONTO, "estado"), estado);
        }
        if (paciente != null) {
            r.addProperty(model.createProperty(ONTO, "paciente"), paciente);
        }
        if (medico != null) {
            r.addProperty(model.createProperty(ONTO, "medico"), medico);
        }
        return r;
    }

    private Resource crearRecursoDiagnostico(Model model, DiagnosticoRemoteDto d, Resource cita, Resource paciente) {
        String iri = BASE_IRI + "diagnostico/" + d.getId();
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "Diagnostico"));
        if (d.getDescripcion() != null) {
            r.addLiteral(model.createProperty(ONTO, "descripcion"), d.getDescripcion());
        }
        if (d.getTipoDiagnostico() != null) {
            Resource tipo = crearRecursoTipoDiagnostico(model, d.getTipoDiagnostico());
            r.addProperty(model.createProperty(ONTO, "tipoDiagnostico"), tipo);
        }
        if (d.getFechaDiagnostico() != null) {
            r.addLiteral(model.createProperty(ONTO, "fechaDiagnostico"), d.getFechaDiagnostico());
        }
        if (cita != null) {
            r.addProperty(model.createProperty(ONTO, "cita"), cita);
        }
        if (paciente != null) {
            r.addProperty(model.createProperty(ONTO, "paciente"), paciente);
        }
        return r;
    }

    private Resource crearRecursoEspecialidadMedico(Model model, String especialidadCodigo) {
        String iri = ONTO + "EspecialidadMedico_" + especialidadCodigo;
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "EspecialidadMedico"));
        return r;
    }

    private Resource crearRecursoEstadoMedico(Model model, String estadoCodigo) {
        String iri = ONTO + "EstadoMedico_" + estadoCodigo;
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "EstadoMedico"));
        return r;
    }

    private Resource crearRecursoEstadoPaciente(Model model, String estadoCodigo) {
        String iri = ONTO + "EstadoPaciente_" + estadoCodigo;
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "EstadoPaciente"));
        return r;
    }

    private Resource crearRecursoEstadoCita(Model model, String estadoCodigo) {
        String iri = ONTO + "EstadoCita_" + estadoCodigo;
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "EstadoCita"));
        return r;
    }

    private Resource crearRecursoTipoDiagnostico(Model model, String tipoCodigo) {
        String iri = ONTO + "TipoDiagnostico_" + tipoCodigo;
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "TipoDiagnostico"));
        return r;
    }

    private Resource crearRecursoHorario(Model model, HorarioRemoteDto h, Resource medico) {
        String iri = BASE_IRI + "horario/" + h.getId();
        Resource r = model.createResource(iri);
        r.addProperty(RDF.type, model.createResource(ONTO + "Horario"));
        if (h.getDiaSemana() != null) {
            r.addLiteral(model.createProperty(ONTO, "diaSemana"), h.getDiaSemana().name());
        }
        if (h.getHoraInicio() != null) {
            r.addLiteral(model.createProperty(ONTO, "horaInicio"), h.getHoraInicio().toString());
        }
        if (h.getHoraFin() != null) {
            r.addLiteral(model.createProperty(ONTO, "horaFin"), h.getHoraFin().toString());
        }
        if (medico != null) {
            r.addProperty(model.createProperty(ONTO, "horarioDe"), medico);
        }
        return r;
    }
}
