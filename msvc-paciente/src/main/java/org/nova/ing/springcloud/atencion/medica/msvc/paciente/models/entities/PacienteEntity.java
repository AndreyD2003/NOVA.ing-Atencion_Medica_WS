package org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.enums.*;

import java.util.Date;
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pacientes")
public class PacienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nombres;
    @NotBlank
    private String apellidos;
    @NotNull
    private Date fechaNacimiento;
    @NotNull
    @Enumerated(EnumType.STRING)
    private GeneroPaciente genero;
    @NotBlank
    @Column(unique = true)
    @Pattern(regexp = "^[1-9][0-9]{7}$")
    private String dni;
    @NotBlank
    @Column(unique = true)
    @Pattern(regexp = "^9[0-9]{8}$")
    private String telefono;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    @NotBlank
    private String direccion;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPaciente estado;
    
    @Column(name = "usuario_id")
    private Long usuarioId;
}
