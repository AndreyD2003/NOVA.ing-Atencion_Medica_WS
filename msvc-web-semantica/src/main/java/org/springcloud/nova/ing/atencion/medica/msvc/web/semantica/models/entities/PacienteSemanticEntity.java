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
@Table(name = "semantic_pacientes")
public class PacienteSemanticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pacienteId;

    private String iri;

    private String nombres;

    private String apellidos;

    private Date fechaNacimiento;

    private String genero;

    private String dni;

    private String telefono;

    private String email;

    private String direccion;

    private String estado;
}

