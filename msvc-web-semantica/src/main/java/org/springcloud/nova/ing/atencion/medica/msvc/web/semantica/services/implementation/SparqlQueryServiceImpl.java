package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.implementation;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Literal;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.RdfGraphService;
import org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.services.SparqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SparqlQueryServiceImpl implements SparqlQueryService {

    @Autowired
    private RdfGraphService rdfGraphService;

    @Override
    public List<Map<String, String>> ejecutarSelectPorCita(Long citaId, String sparql) {
        Model model = rdfGraphService.construirModeloPorCitaId(citaId);
        return ejecutarSelectInterno(model, sparql);
    }

    @Override
    public List<Map<String, String>> ejecutarSelectSistemaCompleto(String sparql) {
        Model model = rdfGraphService.construirModeloSistemaCompleto();
        return ejecutarSelectInterno(model, sparql);
    }

    private List<Map<String, String>> ejecutarSelectInterno(Model model, String sparql) {
        Query query = QueryFactory.create(sparql);
        List<Map<String, String>> resultsList = new ArrayList<>();

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = qexec.execSelect();
            List<String> vars = rs.getResultVars();
            while (rs.hasNext()) {
                QuerySolution sol = rs.next();
                Map<String, String> row = new LinkedHashMap<>();
                for (String v : vars) {
                    if (sol.contains(v) && sol.get(v) != null) {
                        RDFNode node = sol.get(v);
                        if (node.isResource()) {
                            Resource resource = node.asResource();
                            String uri = resource.getURI();
                            if (uri != null && uri.startsWith("http://nova.ing/ontology/")) {
                                row.put(v, uri.substring("http://nova.ing/ontology/".length()).trim());
                            } else if (uri != null && uri.startsWith("http://nova.ing/atencion-medica/")) {
                                row.put(v, uri.substring("http://nova.ing/atencion-medica/".length()).trim());
                            } else {
                                row.put(v, uri != null ? uri.trim() : null);
                            }
                        } else if (node.isLiteral()) {
                            Literal literal = node.asLiteral();
                            row.put(v, literal.getString());
                        } else {
                            row.put(v, node.toString());
                        }
                    } else {
                        row.put(v, null);
                    }
                }
                resultsList.add(row);
            }
        }
        return resultsList;
    }
}
