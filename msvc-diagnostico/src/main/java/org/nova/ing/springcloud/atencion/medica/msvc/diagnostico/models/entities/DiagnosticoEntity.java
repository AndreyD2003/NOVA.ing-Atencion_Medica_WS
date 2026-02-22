package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.enums.TipoDiagnostico;

import java.util.Date;
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "diagnosticos")
public class DiagnosticoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String descripcion;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoDiagnostico tipoDiagnostico;
    @NotNull
    private Date fechaDiagnostico;
    @NotNull
    private Long citaId;
    @NotNull
    private Long pacienteId;

    @Builder.Default
    private boolean activo = true;
}
