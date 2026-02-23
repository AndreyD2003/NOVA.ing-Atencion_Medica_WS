package org.nova.ing.springcloud.atencion.medica.msvc.medico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.entities.MedicoEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.models.HorarioMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.repositories.MedicoRepository;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EspecialidadMedico;
import org.nova.ing.springcloud.atencion.medica.msvc.medico.enums.EstadoMedico;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
				DayOfWeek[] dias = new DayOfWeek[]{
						DayOfWeek.MONDAY,
						DayOfWeek.TUESDAY,
						DayOfWeek.WEDNESDAY,
						DayOfWeek.THURSDAY,
						DayOfWeek.FRIDAY
				};

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

					List<HorarioMedico> horarios = new ArrayList<>();

					DayOfWeek diaBase = dias[(i - 1) % dias.length];

					HorarioMedico manana = new HorarioMedico();
					manana.setDiaSemana(diaBase);
					manana.setHoraInicio(LocalTime.of(9, 0));
					manana.setHoraFin(LocalTime.of(12, 0));
					manana.setMedico(m);
					horarios.add(manana);

					HorarioMedico tarde = new HorarioMedico();
					tarde.setDiaSemana(diaBase);
					tarde.setHoraInicio(LocalTime.of(15, 0));
					tarde.setHoraFin(LocalTime.of(18, 0));
					tarde.setMedico(m);
					horarios.add(tarde);

					m.setHorarios(horarios);

					repository.save(m);
				}
			}
		};
	}

}
