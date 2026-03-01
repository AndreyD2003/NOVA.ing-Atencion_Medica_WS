package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.semantic;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;

import org.springframework.stereotype.Component;

@Component
public class RdfModelBuilder {

    private static final String ONTO = "http://nova.ing/ontology/";
    private static final String AM = "http://nova.ing/atencion-medica/";
    private static final String TDB_DIR = "tdb2_nova_db"; // Directorio local para la base de datos
    private Dataset dataset;

    @PostConstruct
    public void init() {
        // Inicializar TDB2
        dataset = TDB2Factory.connectDataset(TDB_DIR);
        
        // Cargar ontologia si el modelo esta vacio
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            if (model.isEmpty()) {
                System.out.println("Inicializando base de datos TDB2 con ontologia base...");
                ClassPathResource resource = new ClassPathResource("ontology/nova-ontology.owl");
                try (InputStream is = resource.getInputStream()) {
                    model.read(is, ONTO, "RDF/XML");
                }
                model.setNsPrefix("onto", ONTO);
                model.setNsPrefix("am", AM);
                System.out.println("Ontologia cargada exitosamente.");
            }
            dataset.commit();
        } catch (Exception e) {
            dataset.abort();
            System.err.println("Error inicializando TDB2: " + e.getMessage());
        } finally {
            dataset.end();
        }
    }

    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Crea un modelo en memoria temporal, NO persistido.
     * Util para grafos transitorios que no van a la BD.
     */
    public Model createTempModel() {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("onto", ONTO);
        m.setNsPrefix("am", AM);
        return m;
    }

    private String iso(Instant instant) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC).format(instant);
    }
}
