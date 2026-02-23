package org.nova.ing.springcloud.atencion.medica.msvc.medico.repositories;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<MedicoEntity, Long> {
    Optional<MedicoEntity> findByUsuarioId(Long usuarioId);

    @Query("select distinct m from MedicoEntity m left join fetch m.horarios")
    List<MedicoEntity> findAllWithHorarios();

    @Query("select m from MedicoEntity m left join fetch m.horarios where m.id = :id")
    Optional<MedicoEntity> findByIdWithHorarios(@Param("id") Long id);
}
