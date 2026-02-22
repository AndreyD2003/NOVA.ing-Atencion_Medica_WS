package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.repositories.DiagnosticoRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.enums.TipoDiagnostico;
import java.util.Date;

@EnableFeignClients
@SpringBootApplication
public class MsvcDiagnosticoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcDiagnosticoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(DiagnosticoRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				for (int i = 1; i <= 5; i++) {
					DiagnosticoEntity d = new DiagnosticoEntity();
					d.setDescripcion("Diagnostico prueba " + i);
					d.setTipoDiagnostico(TipoDiagnostico.PRESUNTIVO);
					d.setFechaDiagnostico(new Date());
					d.setCitaId((long) i);
					d.setPacienteId((long) i);
					repository.save(d);
				}
			}
		};
	}

}
