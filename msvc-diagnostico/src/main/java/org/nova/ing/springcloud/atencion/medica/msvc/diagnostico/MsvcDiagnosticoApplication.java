package org.nova.ing.springcloud.atencion.medica.msvc.diagnostico;

import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.enums.TipoDiagnostico;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.models.entities.DiagnosticoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.diagnostico.repositories.DiagnosticoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
				// Lista de diagnósticos médicos reales para alimentar la búsqueda semántica
				String[] catalogoDiagnosticos = {
						"Faringoamigdalitis aguda no estreptocócica",
						"Gastritis crónica erosiva en antro",
						"Hipertensión arterial esencial estadio I",
						"Diabetes mellitus tipo 2 sin complicaciones",
						"Anemia ferropénica por deficiencia dietética",
						"Rinitis alérgica estacional",
						"Infección de vías urinarias baja",
						"Lumbalgia mecánica aguda",
						"Asma bronquial persistente leve",
						"Dermatitis atópica severa",
						"Trastorno de ansiedad generalizada",
						"Cefalea tensional crónica",
						"Bronquitis aguda viral",
						"Otitis media serosa",
						"Sindrome de colon irritable",
						"Dengue clásico sin signos de alarma",
						"Obesidad grado II",
						"Hipotiroidismo primario en tratamiento",
						"Conjuntivitis bacteriana aguda",
						"Acné vulgar moderado"
				};

				TipoDiagnostico[] tipos = TipoDiagnostico.values();
				Random random = new Random();
				List<DiagnosticoEntity> diagnosticos = new ArrayList<>();

				for (int i = 1; i <= 50; i++) {
					DiagnosticoEntity d = new DiagnosticoEntity();

					// 1. Descripción realista
					String baseDesc = catalogoDiagnosticos[random.nextInt(catalogoDiagnosticos.length)];
					d.setDescripcion(baseDesc + " (Evaluación #" + i + ")");

					// 2. Tipo de Diagnóstico (PRESUNTIVO, DEFINITIVO, etc.)
					d.setTipoDiagnostico(tipos[random.nextInt(tipos.length)]);

					// 3. Fecha (Sincronizada con el último mes)
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(30));
					d.setFechaDiagnostico(cal.getTime());

					// 4. Relaciones
					// Asignamos el diagnóstico a la cita 'i' (asumiendo que hay 50 citas)
					d.setCitaId((long) i);

					// Asignamos a un paciente aleatorio (1 al 50)
					d.setPacienteId((long) (1 + random.nextInt(50)));

					diagnosticos.add(d);
				}
				repository.saveAll(diagnosticos);
				System.out.println(">>> 50 Diagnósticos médicos reales creados exitosamente.");
			}
		};
	}
}