package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.repositories;

import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.entities.OntologyMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OntologyMetadataRepository extends JpaRepository<OntologyMetadataEntity, Long> {

}

