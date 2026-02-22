package org.nova.ing.springcloud.atencion.medica.msvc.medico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.repositories.MedicoRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EspecialidadMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EstadoMedico;

@EnableFeignClients
@SpringBootApplication
public class MsvcMedicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcMedicoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(MedicoRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				for (int i = 1; i <= 5; i++) {
					MedicoEntity m = new MedicoEntity();
					m.setNombres("Medico " + i);
					m.setApellidos("Apellido " + i);
					m.setEmail("doctor" + i + "@email.com");
					m.setDni(String.valueOf(20000000 + i));
					m.setTelefono(String.valueOf(910000000 + i));
					m.setEspecialidad(EspecialidadMedico.MEDICINA_GENERAL);
					m.setEstado(EstadoMedico.ACTIVO);
					m.setUsuarioId(4L + i); // 5 to 9
					repository.save(m);
				}
			}
		};
	}

}
