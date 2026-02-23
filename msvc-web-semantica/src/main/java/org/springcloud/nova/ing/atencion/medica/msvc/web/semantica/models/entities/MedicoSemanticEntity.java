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

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "semantic_medicos")
public class MedicoSemanticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long medicoId;

    private String iri;

    private String nombres;

    private String apellidos;

    private String especialidad;

    private String telefono;

    private String email;

    private String dni;

    private String estado;
}

