package org.nova.ing.springcloud.atencion.medica.msvc.paciente;

import org.nova.ing.springcloud.atencion.medica.msvc.paciente.enums.EstadoPaciente;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.enums.GeneroPaciente;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.models.entities.PacienteEntity;
import org.nova.ing.springcloud.atencion.medica.msvc.paciente.repositories.PacienteRepository;
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
public class MsvcPacienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcPacienteApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PacienteRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				String[] nombresMasculinos = {"Pedro", "Luis", "Alberto", "Miguel", "Jorge", "Ricardo", "Andrés", "Hugo", "Pablo", "Raúl"};
				String[] nombresFemeninos = {"Ana", "Rosa", "Julia", "Carmen", "Silvia", "Patricia", "Mónica", "Laura", "Lorena", "Andrea"};
				String[] apellidos = {"Silva", "Castro", "Ortiz", "Méndez", "Rojas", "Farfán", "Pinedo", "Villar", "Soto", "Prado",
						"Guzmán", "Flores", "Quispe", "Ramos", "Espinoza", "Benítez", "Vargas", "Cabrera", "Ibarra", "Salinas"};
				String[] distritos = {"Miraflores", "San Isidro", "Lince", "Surco", "La Molina", "San Borja", "Callao", "Magdalena"};

				Random random = new Random();
				List<PacienteEntity> pacientes = new ArrayList<>();

				for (int i = 0; i < 50; i++) {
					PacienteEntity p = new PacienteEntity();
					boolean esMasculino = random.nextBoolean();

					String nombre = esMasculino ?
							nombresMasculinos[random.nextInt(nombresMasculinos.length)] :
							nombresFemeninos[random.nextInt(nombresFemeninos.length)];
					String apellido = apellidos[random.nextInt(apellidos.length)] + " " + apellidos[random.nextInt(apellidos.length)];

					p.setNombres(nombre);
					p.setApellidos(apellido);
					p.setEmail(nombre.toLowerCase() + i + "@gmail.com");
					p.setDni(String.valueOf(70000000 + i));
					p.setTelefono("9" + (20000000 + random.nextInt(79999999)));
					p.setDireccion("Av. " + distritos[random.nextInt(distritos.length)] + " " + (100 + i));
					p.setEstado(EstadoPaciente.ACTIVO);
					p.setGenero(esMasculino ? GeneroPaciente.MASCULINO : GeneroPaciente.FEMENINO);
					p.setUsuarioId(500L + i);

					// Generar fecha de nacimiento aleatoria (entre 1950 y 2015)
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, 1950 + random.nextInt(65));
					cal.set(Calendar.MONTH, random.nextInt(12));
					cal.set(Calendar.DAY_OF_MONTH, 1 + random.nextInt(28));
					p.setFechaNacimiento(cal.getTime());

					pacientes.add(p);
				}
				repository.saveAll(pacientes);
				System.out.println(">>> 50 Pacientes reales creados exitosamente.");
			}
		};
	}
}