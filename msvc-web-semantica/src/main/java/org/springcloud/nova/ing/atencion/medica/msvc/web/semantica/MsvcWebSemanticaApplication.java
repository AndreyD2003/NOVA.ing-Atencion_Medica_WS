package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcWebSemanticaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcWebSemanticaApplication.class, args);
	}

}
