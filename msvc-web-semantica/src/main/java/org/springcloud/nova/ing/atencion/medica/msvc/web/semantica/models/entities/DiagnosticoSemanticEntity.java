package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "semantic_diagnosticos")
public class DiagnosticoSemanticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long diagnosticoId;

    private String iri;

    private String descripcion;

    private String tipoDiagnostico;

    private Date fechaDiagnostico;

    private Long citaId;

    private Long pacienteId;

    private String citaIri;

    private String pacienteIri;
}

