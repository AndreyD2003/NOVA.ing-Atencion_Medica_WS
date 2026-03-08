package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Útil si necesitamos hacer peticiones REST directas a Fuseki
    // para administración (crear datasets, subir archivos .ttl por API)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}