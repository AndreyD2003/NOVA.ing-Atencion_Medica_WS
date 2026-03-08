package org.nova.ing.springcloud.atencion.medica.msvc.medico;

import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EspecialidadMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EstadoMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.HorarioMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.repositories.MedicoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
				String[] nombresBase = {
						"Juan", "Maria", "Carlos", "Elena", "Ricardo", "Sofia", "Fernando", "Lucia", "Gabriel", "Isabel",
						"Alejandro", "Valeria", "Roberto", "Camila", "Javier", "Martina", "Andres", "Daniela", "Jose", "Beatriz"
				};
				String[] apellidosBase = {
						"Garcia", "Rodriguez", "Lopez", "Martinez", "Perez", "Gomez", "Sanchez", "Diaz", "Vasquez", "Castro",
						"Romero", "Suarez", "Mendoza", "Ruiz", "Torres", "Hernandez", "Jimenez", "Moreno", "Alvarez", "Muñoz"
				};

				EspecialidadMedico[] especialidades = EspecialidadMedico.values();
				DayOfWeek[] dias = DayOfWeek.values(); // Lunes a Domingo
				Random random = new Random();

				List<MedicoEntity> medicosParaGuardar = new ArrayList<>();

				for (int i = 0; i < 50; i++) {
					MedicoEntity m = new MedicoEntity();

					// Seleccionamos nombre y apellido de forma que sean variados
					String nombre = nombresBase[i % nombresBase.length];
					String apellido = apellidosBase[(i + 5) % apellidosBase.length];
					if (i >= 20) apellido = apellidosBase[i % apellidosBase.length] + " " + apellidosBase[(i + 2) % apellidosBase.length];

					m.setNombres(nombre);
					m.setApellidos(apellido);
					m.setEmail(nombre.toLowerCase() + "." + apellido.toLowerCase().replace(" ", "") + i + "@novaing.com");
					m.setDni(String.valueOf(40000000 + i));
					m.setTelefono("9" + (10000000 + random.nextInt(89999999)));

					// Rotar especialidades
					m.setEspecialidad(especialidades[i % especialidades.length]);
					m.setEstado(EstadoMedico.ACTIVO);
					m.setUsuarioId((long) (100 + i));

					// Generar horarios (2 a 3 días por médico)
					List<HorarioMedico> horarios = new ArrayList<>();
					int numDias = 2 + random.nextInt(2);

					for (int d = 0; d < numDias; d++) {
						DayOfWeek dia = dias[random.nextInt(5)]; // Solo de Lunes a Viernes para el ejemplo

						// Turno Mañana o Tarde
						HorarioMedico horario = new HorarioMedico();
						horario.setDiaSemana(dia);
						if (random.nextBoolean()) {
							horario.setHoraInicio(LocalTime.of(8, 0));
							horario.setHoraFin(LocalTime.of(13, 0));
						} else {
							horario.setHoraInicio(LocalTime.of(14, 0));
							horario.setHoraFin(LocalTime.of(19, 0));
						}
						horario.setMedico(m);
						horarios.add(horario);
					}
					m.setHorarios(horarios);
					medicosParaGuardar.add(m);
				}
				repository.saveAll(medicosParaGuardar);
				System.out.println(">>> 50 Médicos reales creados exitosamente.");
			}
		};
	}
}