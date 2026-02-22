package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.repositories;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosticoRepository extends JpaRepository<DiagnosticoEntity, Long> {
    List<DiagnosticoEntity> findByCitaId(Long citaId);
    List<DiagnosticoEntity> findByPacienteId(Long pacienteId);
}
