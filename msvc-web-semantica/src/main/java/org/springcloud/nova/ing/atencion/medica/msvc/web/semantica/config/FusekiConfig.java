package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Getter
public class FusekiConfig {

    private static final Logger log = LoggerFactory.getLogger(FusekiConfig.class);

    @Value("${jena.fuseki.query-url}")
    private String queryUrl;

    @Value("${jena.fuseki.update-url}")
    private String updateUrl;

    @PostConstruct
    public void init() {
        log.info("###### Configuración de Jena Fuseki ######");
        log.info("Query URL: {}", queryUrl);
        log.info("Update URL: {}", updateUrl);
        log.info("###########################################");

        if (queryUrl == null || updateUrl == null) {
            log.error("¡ERROR: No se han configurado las URLs de Fuseki en application.properties!");
        }
    }
}