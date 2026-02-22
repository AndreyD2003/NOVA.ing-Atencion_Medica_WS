package org.nova.ing.springcloud.atencion.medica.msvc.cita.repositories;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<CitaEntity, Long> {
    List<CitaEntity> findByPacienteId(Long pacienteId);
    List<CitaEntity> findByMedicoId(Long medicoId);

    @Query("SELECT COUNT(c) > 0 FROM CitaEntity c WHERE c.medicoId = :medicoId AND c.fechaCita = :fechaCita AND c.estado NOT IN ('CANCELADA', 'REALIZADA') AND (c.horaInicio < :horaFin AND c.horaFin > :horaInicio) AND (:id IS NULL OR c.id <> :id)")
    boolean existsConflictMedico(@Param("medicoId") Long medicoId, @Param("fechaCita") Date fechaCita, @Param("horaInicio") Time horaInicio, @Param("horaFin") Time horaFin, @Param("id") Long id);

    @Query("SELECT COUNT(c) > 0 FROM CitaEntity c WHERE c.pacienteId = :pacienteId AND c.fechaCita = :fechaCita AND c.estado NOT IN ('CANCELADA', 'REALIZADA') AND (c.horaInicio < :horaFin AND c.horaFin > :horaInicio) AND (:id IS NULL OR c.id <> :id)")
    boolean existsConflictPaciente(@Param("pacienteId") Long pacienteId, @Param("fechaCita") Date fechaCita, @Param("horaInicio") Time horaInicio, @Param("horaFin") Time horaFin, @Param("id") Long id);
}
