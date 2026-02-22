package org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.enums.EstadoCita;

import java.sql.Time;
import java.util.*;
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "citas")
public class CitaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Date fechaCita;
    @NotNull
    private Time horaInicio;
    @NotNull
    private Time horaFin;
    @NotBlank
    private String motivo;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoCita estado;

    @NotNull
    private Long pacienteId;
    @NotNull
    private Long medicoId;
}
