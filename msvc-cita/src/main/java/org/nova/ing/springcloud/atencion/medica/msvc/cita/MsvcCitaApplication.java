package org.nova.ing.springcloud.atencion.medica.msvc.cita;

import org.nova.ing.springcloud.atencion.medica.msvc.cita.enums.EstadoCita;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.models.entities.CitaEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.cita.repositories.CitaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
				String[] motivos = {
						"Consulta de Medicina General", "Control de Hipertensión", "Chequeo Preventivo Anual",
						"Dolor Abdominal Agudo", "Seguimiento de Tratamiento", "Evaluación de Resultados de Laboratorio",
						"Migraña Crónica", "Control de Diabetes Tipo 2", "Consulta de Especialidad",
						"Dolor Lumbar", "Infección Respiratoria", "Evaluación Pre-operatoria",
						"Control de Colesterol", "Alergia Estacional", "Control de Peso y Nutrición"
				};

				Random random = new Random();
				List<CitaEntity> citas = new ArrayList<>();
				Date hoy = new Date();

				for (int i = 1; i <= 50; i++) {
					CitaEntity c = new CitaEntity();

					// 1. Fecha (Rango de 30 días antes a 30 días después)
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_YEAR, random.nextInt(60) - 30);
					Date fechaCita = cal.getTime();
					c.setFechaCita(fechaCita);

					// 2. Horarios
					int hora = 8 + random.nextInt(9);
					int min = random.nextBoolean() ? 0 : 30;
					c.setHoraInicio(Time.valueOf(String.format("%02d:%02d:00", hora, min)));

					int horaFin = (min == 30) ? hora + 1 : hora;
					int minFin = (min == 30) ? 0 : 30;
					c.setHoraFin(Time.valueOf(String.format("%02d:%02d:00", horaFin, minFin)));

					// 3. Motivo
					c.setMotivo(motivos[random.nextInt(motivos.length)]);

					// 4. Lógica de ESTADOS corregida según tu Enum
					if (fechaCita.before(hoy)) {
						// Si la fecha ya pasó, la mayoría son REALIZADA, algunas CANCELADA
						c.setEstado(random.nextDouble() > 0.15 ? EstadoCita.REALIZADA : EstadoCita.CANCELADA);
					} else {
						// Si es a futuro, están PROGRAMADA o REPROGRAMADA
						c.setEstado(random.nextDouble() > 0.2 ? EstadoCita.PROGRAMADA : EstadoCita.REPROGRAMADA);
					}

					// 5. Relaciones con IDs de Médicos y Pacientes (1 al 50)
					c.setPacienteId((long) (1 + random.nextInt(50)));
					c.setMedicoId((long) (1 + random.nextInt(50)));

					citas.add(c);
				}
				repository.saveAll(citas);
				System.out.println(">>> 50 Citas creadas con estados: PROGRAMADA, REALIZADA, CANCELADA, REPROGRAMADA.");
			}
		};
	}
}