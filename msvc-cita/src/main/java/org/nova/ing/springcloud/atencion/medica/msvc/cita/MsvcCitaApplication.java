package org.nova.ing.springcloud.atencion.medica.msvc.cita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.repositories.CitaRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.enums.EstadoCita;
import java.util.Date;
import java.sql.Time;

@EnableFeignClients
@SpringBootApplication
public class MsvcCitaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcCitaApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(CitaRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				for (int i = 1; i <= 5; i++) {
					CitaEntity c = new CitaEntity();
					c.setFechaCita(new Date());
					c.setHoraInicio(Time.valueOf("10:00:00"));
					c.setHoraFin(Time.valueOf("10:30:00"));
					c.setMotivo("Consulta General " + i);
					c.setEstado(EstadoCita.PROGRAMADA);
					c.setPacienteId((long) i);
		 			c.setMedicoId((long) i);
					repository.save(c);
				}
			}
		};
	}

}
