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
@Table(name = "semantic_citas")
public class CitaSemanticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long citaId;

    private String iri;

    private Date fechaCita;

    private String horaInicio;

    private String horaFin;

    private String motivo;

    private String estado;

    private Long pacienteId;

    private Long medicoId;

    private String pacienteIri;

    private String medicoIri;
}

