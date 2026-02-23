package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.implementation;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients.CitaClientRest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.CitaSemanticaDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.DiagnosticoSemanticoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.GrafoClinicoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.MedicoSemanticoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.PacienteSemanticoDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.CitaDetalleRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.CitaRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.DiagnosticoRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.MedicoRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.dto.remote.PacienteRemoteDto;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SemanticGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SemanticGraphServiceImpl implements SemanticGraphService {

    private static final String BASE_IRI = "http://nova.ing/atencion-medica/";

    @Autowired
    private CitaClientRest citaClientRest;

    @Override
    public GrafoClinicoDto construirGrafoPorCitaId(Long citaId) {
        CitaDetalleRemoteDto detalle = citaClientRest.obtenerCitaConDetalle(citaId);
        if (detalle == null || detalle.getCita() == null) {
            throw new IllegalStateException("No se encontró la cita con id " + citaId);
        }

        CitaRemoteDto cita = detalle.getCita();
        PacienteRemoteDto paciente = detalle.getPaciente();
        MedicoRemoteDto medico = detalle.getMedico();
        List<DiagnosticoRemoteDto> diagnosticos = detalle.getDiagnosticos();

        PacienteSemanticoDto pacienteSemantico = mapPaciente(paciente);
        MedicoSemanticoDto medicoSemantico = mapMedico(medico);
        CitaSemanticaDto citaSemantica = mapCita(cita, pacienteSemantico, medicoSemantico);
        List<DiagnosticoSemanticoDto> diagnosticosSemanticos = mapDiagnosticos(diagnosticos, citaSemantica, pacienteSemantico);

        GrafoClinicoDto grafo = new GrafoClinicoDto();
        grafo.setPaciente(pacienteSemantico);
        grafo.setMedico(medicoSemantico);
        grafo.setCita(citaSemantica);
        grafo.setDiagnosticos(diagnosticosSemanticos);
        return grafo;
    }

    private PacienteSemanticoDto mapPaciente(PacienteRemoteDto source) {
        if (source == null) {
            return null;
        }
        PacienteSemanticoDto target = new PacienteSemanticoDto();
        target.setPacienteId(source.getId());
        target.setIri(crearIri("paciente", source.getId()));
        target.setNombres(source.getNombres());
        target.setApellidos(source.getApellidos());
        target.setFechaNacimiento(source.getFechaNacimiento());
        target.setGenero(source.getGenero());
        target.setDni(source.getDni());
        target.setTelefono(source.getTelefono());
        target.setEmail(source.getEmail());
        target.setDireccion(source.getDireccion());
        target.setEstado(source.getEstado());
        return target;
    }

    private MedicoSemanticoDto mapMedico(MedicoRemoteDto source) {
        if (source == null) {
            return null;
        }
        MedicoSemanticoDto target = new MedicoSemanticoDto();
        target.setMedicoId(source.getId());
        target.setIri(crearIri("medico", source.getId()));
        target.setNombres(source.getNombres());
        target.setApellidos(source.getApellidos());
        target.setEspecialidad(source.getEspecialidad());
        target.setTelefono(source.getTelefono());
        target.setEmail(source.getEmail());
        target.setDni(source.getDni());
        target.setEstado(source.getEstado());
        return target;
    }

    private CitaSemanticaDto mapCita(CitaRemoteDto source, PacienteSemanticoDto paciente, MedicoSemanticoDto medico) {
        if (source == null) {
            return null;
        }
        CitaSemanticaDto target = new CitaSemanticaDto();
        target.setCitaId(source.getId());
        target.setIri(crearIri("cita", source.getId()));
        target.setFechaCita(source.getFechaCita());
        target.setHoraInicio(source.getHoraInicio());
        target.setHoraFin(source.getHoraFin());
        target.setMotivo(source.getMotivo());
        target.setEstado(source.getEstado());
        target.setPacienteId(source.getPacienteId());
        target.setMedicoId(source.getMedicoId());
        if (paciente != null) {
            target.setPacienteIri(paciente.getIri());
        }
        if (medico != null) {
            target.setMedicoIri(medico.getIri());
        }
        return target;
    }

    private List<DiagnosticoSemanticoDto> mapDiagnosticos(List<DiagnosticoRemoteDto> source,
                                                          CitaSemanticaDto cita,
                                                          PacienteSemanticoDto paciente) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .filter(Objects::nonNull)
                .map(d -> mapDiagnostico(d, cita, paciente))
                .collect(Collectors.toList());
    }

    private DiagnosticoSemanticoDto mapDiagnostico(DiagnosticoRemoteDto source,
                                                   CitaSemanticaDto cita,
                                                   PacienteSemanticoDto paciente) {
        DiagnosticoSemanticoDto target = new DiagnosticoSemanticoDto();
        target.setDiagnosticoId(source.getId());
        target.setIri(crearIri("diagnostico", source.getId()));
        target.setDescripcion(source.getDescripcion());
        target.setTipoDiagnostico(source.getTipoDiagnostico());
        target.setFechaDiagnostico(source.getFechaDiagnostico());
        target.setCitaId(source.getCitaId());
        target.setPacienteId(source.getPacienteId());
        if (cita != null) {
            target.setCitaIri(cita.getIri());
        }
        if (paciente != null) {
            target.setPacienteIri(paciente.getIri());
        }
        return target;
    }

    private String crearIri(String tipo, Long id) {
        if (id == null) {
            return null;
        }
        return BASE_IRI + tipo + "/" + id;
    }
}
