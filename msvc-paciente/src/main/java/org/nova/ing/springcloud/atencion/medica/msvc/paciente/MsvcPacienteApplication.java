package org.nova.ing.springcloud.atencion.medica.msvc.paciente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities.PacienteEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.repositories.PacienteRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.enums.GeneroPaciente;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.enums.EstadoPaciente;
import java.util.Date;

@EnableFeignClients
@SpringBootApplication
public class MsvcPacienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcPacienteApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PacienteRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				for (int i = 1; i <= 5; i++) {
					PacienteEntity p = new PacienteEntity();
					p.setNombres("Paciente " + i);
					p.setApellidos("Apellido " + i);
					p.setEmail("patient" + i + "@email.com");
					p.setDni(String.valueOf(10000000 + i));
					p.setFechaNacimiento(new Date());
					p.setGenero(GeneroPaciente.MASCULINO);
					p.setTelefono(String.valueOf(900000000 + i));
					p.setDireccion("Direccion " + i);
					p.setEstado(EstadoPaciente.ACTIVO);
					p.setUsuarioId(9L + i); // 10 to 14
					repository.save(p);
				}
			}
		};
	}

}
