package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public final class MED {
    // Namespace de nuestra ontología médica
    public static final String NS = "http://org.nova.atencion.medica/ontologia#";

    // --- CLASES ---
    public static final Resource Paciente    = ResourceFactory.createResource(NS + "Paciente");
    public static final Resource Medico      = ResourceFactory.createResource(NS + "Medico");
    public static final Resource Cita        = ResourceFactory.createResource(NS + "Cita");
    public static final Resource Diagnostico = ResourceFactory.createResource(NS + "Diagnostico");

    // --- PROPIEDADES DE PACIENTE ---
    public static final Property dniPaciente    = ResourceFactory.createProperty(NS, "dniPaciente");
    public static final Property nombreCompleto = ResourceFactory.createProperty(NS, "nombreCompleto");
    public static final Property genero         = ResourceFactory.createProperty(NS, "genero");
    public static final Property emailPaciente  = ResourceFactory.createProperty(NS, "emailPaciente");

    // --- PROPIEDADES DE MÉDICO ---
    public static final Property dniMedico      = ResourceFactory.createProperty(NS, "dniMedico");
    public static final Property especialidad   = ResourceFactory.createProperty(NS, "especialidad");
    public static final Property nombreMedico    = ResourceFactory.createProperty(NS, "nombreMedico");

    // --- PROPIEDADES DE CITA ---
    public static final Property fechaCita      = ResourceFactory.createProperty(NS, "fechaCita");
    public static final Property motivoCita     = ResourceFactory.createProperty(NS, "motivoCita");
    public static final Property estadoCita     = ResourceFactory.createProperty(NS, "estadoCita");

    // --- PROPIEDADES DE DIAGNÓSTICO ---
    public static final Property descripcionDiag = ResourceFactory.createProperty(NS, "descripcionDiag");
    public static final Property tipoDiag        = ResourceFactory.createProperty(NS, "tipoDiag");

    // --- RELACIONES (Object Properties) ---
    public static final Property citaAgendadaPara = ResourceFactory.createProperty(NS, "citaAgendadaPara"); // Cita -> Paciente
    public static final Property atendidaPor      = ResourceFactory.createProperty(NS, "atendidaPor");      // Cita -> Medico
    public static final Property generaDiagnostico  = ResourceFactory.createProperty(NS, "generaDiagnostico"); // Cita -> Diagnostico

    private MED() {} // Evitar instanciación
}