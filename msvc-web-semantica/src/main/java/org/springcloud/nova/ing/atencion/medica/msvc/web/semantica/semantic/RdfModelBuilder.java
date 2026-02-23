package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.semantic;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.CitaSemanticaDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.DiagnosticoSemanticoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.GrafoClinicoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.MedicoSemanticoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.PacienteSemanticoDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class RdfModelBuilder {

    private static final String ONTO = "http://nova.ing/ontology/";
    private static final String AM = "http://nova.ing/atencion-medica/";

    public Model createEmptyModel() {
        return ModelFactory.createDefaultModel();
    }

    public Model fromGrafoClinico(GrafoClinicoDto grafo) {
        Model m = createEmptyModel();
        m.setNsPrefix("onto", ONTO);
        m.setNsPrefix("am", AM);
        Resource paciente = addPaciente(m, grafo.getPaciente());
        Resource medico = addMedico(m, grafo.getMedico());
        Resource cita = addCita(m, grafo.getCita(), paciente, medico);
        addDiagnosticos(m, grafo.getDiagnosticos(), cita, paciente);
        return m;
    }

    private Resource addPaciente(Model m, PacienteSemanticoDto p) {
        if (p == null) return null;
        Resource r = m.createResource(p.getIri());
        r.addProperty(RDF.type, m.createResource(ONTO + "Paciente"));
        addLiteral(m, r, "nombres", p.getNombres());
        addLiteral(m, r, "apellidos", p.getApellidos());
        if (p.getFechaNacimiento() != null) {
            r.addLiteral(m.createProperty(ONTO, "fechaNacimiento"),
                    m.createTypedLiteral(iso(p.getFechaNacimiento().toInstant()), XSDDatatype.XSDdateTime));
        }
        addLiteral(m, r, "genero", p.getGenero());
        addLiteral(m, r, "dni", p.getDni());
        addLiteral(m, r, "telefono", p.getTelefono());
        addLiteral(m, r, "email", p.getEmail());
        addLiteral(m, r, "direccion", p.getDireccion());
        if (p.getEstado() != null) {
            Resource estado = m.createResource(ONTO + "EstadoPaciente_" + p.getEstado());
            estado.addProperty(RDF.type, m.createResource(ONTO + "EstadoPaciente"));
            r.addProperty(m.createProperty(ONTO, "estado"), estado);
        }
        return r;
    }

    private Resource addMedico(Model m, MedicoSemanticoDto me) {
        if (me == null) return null;
        Resource r = m.createResource(me.getIri());
        r.addProperty(RDF.type, m.createResource(ONTO + "Medico"));
        addLiteral(m, r, "nombres", me.getNombres());
        addLiteral(m, r, "apellidos", me.getApellidos());
        if (me.getEspecialidad() != null) {
            Resource esp = m.createResource(ONTO + "EspecialidadMedico_" + me.getEspecialidad());
            esp.addProperty(RDF.type, m.createResource(ONTO + "EspecialidadMedico"));
            r.addProperty(m.createProperty(ONTO, "especialidad"), esp);
        }
        addLiteral(m, r, "telefono", me.getTelefono());
        addLiteral(m, r, "email", me.getEmail());
        addLiteral(m, r, "dni", me.getDni());
        if (me.getEstado() != null) {
            Resource estado = m.createResource(ONTO + "EstadoMedico_" + me.getEstado());
            estado.addProperty(RDF.type, m.createResource(ONTO + "EstadoMedico"));
            r.addProperty(m.createProperty(ONTO, "estado"), estado);
        }
        return r;
    }

    private Resource addCita(Model m, CitaSemanticaDto c, Resource paciente, Resource medico) {
        if (c == null) return null;
        Resource r = m.createResource(c.getIri());
        r.addProperty(RDF.type, m.createResource(ONTO + "Cita"));
        if (c.getFechaCita() != null) {
            r.addLiteral(m.createProperty(ONTO, "fechaCita"),
                    m.createTypedLiteral(iso(c.getFechaCita().toInstant()), XSDDatatype.XSDdateTime));
        }
        addLiteral(m, r, "horaInicio", c.getHoraInicio());
        addLiteral(m, r, "horaFin", c.getHoraFin());
        addLiteral(m, r, "motivo", c.getMotivo());
        if (c.getEstado() != null) {
            Resource estado = m.createResource(ONTO + "EstadoCita_" + c.getEstado());
            estado.addProperty(RDF.type, m.createResource(ONTO + "EstadoCita"));
            r.addProperty(m.createProperty(ONTO, "estado"), estado);
        }
        if (paciente != null) {
            r.addProperty(m.createProperty(ONTO, "paciente"), paciente);
        }
        if (medico != null) {
            r.addProperty(m.createProperty(ONTO, "medico"), medico);
        }
        return r;
    }

    private void addDiagnosticos(Model m, List<DiagnosticoSemanticoDto> ds, Resource cita, Resource paciente) {
        if (ds == null) return;
        for (DiagnosticoSemanticoDto d : ds) {
            Resource r = m.createResource(d.getIri());
            r.addProperty(RDF.type, m.createResource(ONTO + "Diagnostico"));
            addLiteral(m, r, "descripcion", d.getDescripcion());
            if (d.getTipoDiagnostico() != null) {
                Resource tipo = m.createResource(ONTO + "TipoDiagnostico_" + d.getTipoDiagnostico());
                tipo.addProperty(RDF.type, m.createResource(ONTO + "TipoDiagnostico"));
                r.addProperty(m.createProperty(ONTO, "tipoDiagnostico"), tipo);
            }
            if (d.getFechaDiagnostico() != null) {
                r.addLiteral(m.createProperty(ONTO, "fechaDiagnostico"),
                        m.createTypedLiteral(iso(d.getFechaDiagnostico().toInstant()), XSDDatatype.XSDdateTime));
            }
            if (cita != null) {
                r.addProperty(m.createProperty(ONTO, "cita"), cita);
            }
            if (paciente != null) {
                r.addProperty(m.createProperty(ONTO, "paciente"), paciente);
            }
        }
    }

    private void addLiteral(Model m, Resource r, String localName, String value) {
        if (value == null) return;
        r.addLiteral(m.createProperty(ONTO, localName), value);
    }

    private String iso(Instant instant) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC).format(instant);
    }
}
