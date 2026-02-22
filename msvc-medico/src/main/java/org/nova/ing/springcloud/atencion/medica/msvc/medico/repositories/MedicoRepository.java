package org.nova.ing.springcloud.atencion.medica.msvc.medico.repositories;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<MedicoEntity, Long> {
    Optional<MedicoEntity> findByUsuarioId(Long usuarioId);
}
