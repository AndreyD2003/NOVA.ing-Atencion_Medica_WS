package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class QuerySemanticTest {

    private QueryParser queryParser;
    private SparqlBuilder sparqlBuilder;

    @BeforeEach
    void setUp() {
        TextCleaner cleaner = new TextCleaner();
        queryParser = new QueryParser(cleaner);
        sparqlBuilder = new SparqlBuilder();
    }

    @Test
    void testBusquedaDNI() {
        String texto = "citas del paciente 70000001";
        ParseResult result = queryParser.parse(texto);
        
        assertEquals("70000001", result.getDni());
        
        String sparql = sparqlBuilder.buildSearchQuery(result);
        assertTrue(sparql.contains("OPTIONAL { ?pUri med:dniPaciente ?dni_paciente }"));
        assertTrue(sparql.contains("FILTER(COALESCE(STR(?dni_paciente), \"\") = \"70000001\" || COALESCE(STR(?dni_medico), \"\") = \"70000001\")"));
    }

    @Test
    void testNuevosFormatosFecha() {
        // Formato dd/MM/yyyy
        String texto1 = "citas del 26/02/2024";
        ParseResult r1 = queryParser.parse(texto1);
        assertEquals("2024-02-26", r1.getFechaInicio());

        // Formato dd-MM-yyyy
        String texto2 = "citas del 26-02-2024";
        ParseResult r2 = queryParser.parse(texto2);
        assertEquals("2024-02-26", r2.getFechaInicio());
    }

    @Test
    void testRangoFechasInvertido() {
        // Fecha fin anterior a fecha inicio: el parser debe invertirlas
        String texto = "citas del 03/03/2024 al 26/02/2024";
        ParseResult result = queryParser.parse(texto);
        
        assertEquals("2024-02-26", result.getFechaInicio());
        assertEquals("2024-03-03", result.getFechaFin());
    }

    @Test
    void testCitasHoy() {
        String texto = "citas de hoy";
        ParseResult result = queryParser.parse(texto);
        String hoy = LocalDate.now().toString();
        
        assertEquals(hoy, result.getFechaInicio());
        
        String sparql = sparqlBuilder.buildSearchQuery(result);
        assertTrue(sparql.contains("FILTER(xsd:date(STR(?fecha)) = \"" + hoy + "\"^^xsd:date)"));
    }

    @Test
    void testCardiologiaHoyProgramada() {
        String texto = "cardiologias programadas para hoy";
        ParseResult result = queryParser.parse(texto);
        String hoy = LocalDate.now().toString();
        
        assertEquals("CARDIOLOGIA", result.getEspecialidad());
        assertEquals("PROGRAMADA", result.getEstado());
        assertEquals(hoy, result.getFechaInicio());
        
        String sparql = sparqlBuilder.buildSearchQuery(result);
        assertTrue(sparql.contains("FILTER(LCASE(STR(?especialidad)) = \"cardiologia\")"));
        assertTrue(sparql.contains("FILTER(LCASE(STR(?estado)) = \"programada\")"));
        assertTrue(sparql.contains("FILTER(xsd:date(STR(?fecha)) = \"" + hoy + "\"^^xsd:date)"));
    }

    @Test
    void testCitasEstaSemana() {
        String texto = "citas de esta semana";
        ParseResult result = queryParser.parse(texto);
        
        assertNotNull(result.getFechaInicio());
        assertNotNull(result.getFechaFin());
        
        String sparql = sparqlBuilder.buildSearchQuery(result);
        assertTrue(sparql.contains("xsd:date(STR(?fecha)) >= \"" + result.getFechaInicio() + "\"^^xsd:date"));
        assertTrue(sparql.contains("xsd:date(STR(?fecha)) <= \"" + result.getFechaFin() + "\"^^xsd:date"));
    }

    @Test
    void testCitasEntreFechas() {
        String texto = "citas entre 2026-03-01 y 2026-03-10";
        ParseResult result = queryParser.parse(texto);
        
        assertEquals("2026-03-01", result.getFechaInicio());
        assertEquals("2026-03-10", result.getFechaFin());
        
        String sparql = sparqlBuilder.buildSearchQuery(result);
        assertTrue(sparql.contains(">= \"2026-03-01\"^^xsd:date"));
        assertTrue(sparql.contains("<= \"2026-03-10\"^^xsd:date"));
    }
}
