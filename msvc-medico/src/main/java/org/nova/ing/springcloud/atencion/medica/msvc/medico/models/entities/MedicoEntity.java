package org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EspecialidadMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EstadoMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.HorarioMedico;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicos")
public class MedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombres;

    @NotBlank
    @Column(nullable = false)
    private String apellidos;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecialidadMedico especialidad;

    @NotBlank
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^9[0-9]{8}$")
    private String telefono;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[1-9][0-9]{7}$")
    private String dni;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMedico estado;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<HorarioMedico> horarios;

    @Column(name = "usuario_id")
    private Long usuarioId;
}
