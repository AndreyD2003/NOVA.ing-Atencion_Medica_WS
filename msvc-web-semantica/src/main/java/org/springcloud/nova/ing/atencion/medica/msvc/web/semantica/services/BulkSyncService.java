package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.clients.*;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.DiagnosticoSyncDTO;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.SyncRequestDTO;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.dtos.load.*; // Importa tus nuevos LoadDTOs
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.repositories.FusekiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkSyncService {

    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;
    private final CitaClient citaClient;
    private final DiagnosticoClient diagnosticoClient;
    private final ISemanticService semanticService;
    private final FusekiRepository fusekiRepository;

    public void sincronizarTodoElSistema() {
        log.info("### INICIANDO CARGA MASIVA AL GRAFO SEMÁNTICO ###");

        try {
            // 0. LIMPIAR GRAFO COMPLETO para evitar duplicados acumulados
            log.info("Limpiando grafo existente...");
            fusekiRepository.executeUpdate("CLEAR DEFAULT");
            log.info("Grafo limpio. Insertando datos frescos...");

            // 1. CARGAR PACIENTES - Usamos POJOs para evitar conflictos con Jena
            log.info("Descargando pacientes...");
            Map<Long, PacienteLoadDTO> pacientesMap = pacienteClient.listarTodos().stream()
                    .collect(Collectors.toMap(PacienteLoadDTO::getId, p -> p));

            // 2. CARGAR MÉDICOS
            log.info("Descargando médicos...");
            Map<Long, MedicoLoadDTO> medicosMap = medicoClient.listarTodos().stream()
                    .collect(Collectors.toMap(MedicoLoadDTO::getId, m -> m));

            // 3. PROCESAR CITAS
            log.info("Procesando Citas...");
            List<CitaLoadDTO> citas = citaClient.listarTodas();

            for (CitaLoadDTO c : citas) {
                PacienteLoadDTO paciente = pacientesMap.get(c.getPacienteId());
                MedicoLoadDTO medico = medicosMap.get(c.getMedicoId());

                if (paciente != null && medico != null) {
                    // Extraer solo yyyy-MM-dd de la fecha (puede venir como ISO completo)
                    String fechaLimpia = c.getFechaCita() != null && c.getFechaCita().length() >= 10
                            ? c.getFechaCita().substring(0, 10)
                            : c.getFechaCita();

                    SyncRequestDTO dto = SyncRequestDTO.builder()
                            .citaId(c.getId())
                            .fechaCita(fechaLimpia)
                            .horaInicio(c.getHoraInicio())
                            .horaFin(c.getHoraFin())
                            .motivo(c.getMotivo())
                            .estadoCita(c.getEstado())
                            .pacienteId(paciente.getId())
                            .dniPaciente(paciente.getDni())
                            .nombrePaciente(paciente.getNombres())
                            .apellidoPaciente(paciente.getApellidos())
                            .medicoId(medico.getId())
                            .dniMedico(medico.getDni())
                            .nombreMedico(medico.getNombres())
                            .especialidad(medico.getEspecialidad())
                            .build();

                    semanticService.sincronizarAtencionMedica(dto);
                }
            }

            // 4. PROCESAR DIAGNÓSTICOS
            log.info("Procesando Diagnósticos...");
            List<DiagnosticoLoadDTO> diagnosticos = diagnosticoClient.listarTodos();

            for (DiagnosticoLoadDTO d : diagnosticos) {
                DiagnosticoSyncDTO dto = new DiagnosticoSyncDTO();
                dto.setId(d.getId());
                dto.setCitaId(d.getCitaId());
                dto.setDescripcion(d.getDescripcion());
                dto.setTipoDiagnostico(d.getTipoDiagnostico());

                semanticService.sincronizarDiagnostico(dto);
            }

            log.info("### CARGA MASIVA FINALIZADA CON ÉXITO ###");

        } catch (Exception e) {
            log.error("Error crítico durante la carga masiva: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en sincronización masiva: " + e.getMessage());
        }
    }
}