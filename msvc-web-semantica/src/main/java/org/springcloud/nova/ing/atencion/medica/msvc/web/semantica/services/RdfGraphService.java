package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services;

import org.apache.jena.rdf.model.Model;

public interface RdfGraphService {

    /**
     * Sincroniza los datos desde los microservicios externos hacia la base de datos RDF local (TDB2).
     * Esta operacion puede ser costosa y debe ejecutarse asincronamente o bajo demanda.
     */
    void sincronizarDatosSistema();

    /**
     * Obtiene una vista de lectura del modelo RDF completo almacenado en TDB2.
     * Ideal para consultas SPARQL rapidas.
     */
    Model obtenerModeloLectura();

    /**
     * Ejecuta una consulta SPARQL SELECT sobre el dataset persistente.
     * Maneja internamente la transacción de lectura de TDB2.
     */
    java.util.List<java.util.Map<String, String>> ejecutarConsultaSparql(String sparql);

    String serializarModeloSistemaCompleto(String formato);
}

