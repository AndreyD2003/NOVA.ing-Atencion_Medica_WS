package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.repositories;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateExecution;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions.FusekiConnectionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class FusekiRepository {

    @Value("${jena.fuseki.query-url}")
    private String queryUrl;

    @Value("${jena.fuseki.update-url}")
    private String updateUrl;

    private static final int TIMEOUT_SECONDS = 10;

    /**
     * Ejecuta una consulta SELECT remota en Fuseki.
     * Utiliza la Fluent API de Jena 5 con timeouts configurados.
     */
    public List<Map<String, String>> executeSelect(String sparqlQuery) {
        List<Map<String, String>> resultsList = new ArrayList<>();

        log.info("Ejecutando SELECT en: {}", queryUrl);
        log.debug("Query SPARQL:\n{}", sparqlQuery);

        // En Jena 5, se utiliza QueryExecution.service() para consultas remotas
        try (QueryExecution qExec = QueryExecution.service(queryUrl)
                .query(sparqlQuery)
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()) {

            ResultSet rs = qExec.execSelect();

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                Map<String, String> row = new HashMap<>();

                // Extraer dinámicamente todas las variables del SELECT (?cita, ?paciente, etc)
                soln.varNames().forEachRemaining(var -> {
                    RDFNode node = soln.get(var);
                    if (node != null) {
                        if (node.isLiteral()) {
                            row.put(var, node.asLiteral().getLexicalForm());
                        } else {
                            row.put(var, node.toString());
                        }
                    }
                });
                resultsList.add(row);
            }
        } catch (Exception e) {
            log.error("Error crítico en Fuseki SELECT: {} (URL: {})", e.getMessage(), queryUrl, e);
            throw new FusekiConnectionException(
                    String.format("Error de comunicación con Fuseki en %s: %s. Verifique que el servidor esté activo y el dataset 'atencion_medica' exista.", 
                            queryUrl, e.getMessage())
            );
        }
        return resultsList;
    }

    /**
     * Ejecuta una operación de actualización (INSERT/DELETE) remota en Fuseki.
     * Utiliza la Fluent API de Jena 5 para Update remotos con timeouts.
     */
    public void executeUpdate(String sparqlUpdate) {
        log.info("Ejecutando UPDATE en: {}", updateUrl);
        log.debug("Update SPARQL:\n{}", sparqlUpdate);

        try {
            UpdateRequest request = UpdateFactory.create(sparqlUpdate);

            UpdateExecution.service(updateUrl)
                    .update(request)
                    .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .execute();

            log.info("Operación SPARQL Update ejecutada con éxito.");

        } catch (Exception e) {
            log.error("Error crítico en Fuseki UPDATE: {} (URL: {})", e.getMessage(), updateUrl, e);
            throw new FusekiConnectionException(
                    String.format("Error al enviar actualización a Fuseki en %s: %s. Verifique conectividad y permisos.", 
                            updateUrl, e.getMessage())
            );
        }
    }
}