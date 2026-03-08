package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.parsers;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Resultado inmutable del parsing de una consulta en lenguaje natural.
 * Encapsula todos los filtros y la intención detectada.
 */
@Getter
@Builder
@ToString
public class ParseResult {

    /** Instancia vacía para consultas nulas o en blanco */
    public static final ParseResult EMPTY = ParseResult.builder().limite(5).build();

    // ── Filtros extraídos ──
    private final String dni;
    private final String especialidad;
    private final String estado;
    private final String fechaInicio;
    private final String fechaFin;
    private final String horaBusqueda;
    private final String nombreBusqueda;
    private final String tipoDiagnostico;

    // ── Intención ──
    private final boolean esRankingMedicos;
    private final boolean esBusquedaDisponibilidad;
    private final boolean ordenAscendente;
    private final int limite;
    // En ParseResult.java agregar el campo:
    private final boolean esHistorial;

    /** Retorna true si no se extrajo ningún filtro ni intención especial */
    public boolean sinFiltros() {
        return dni == null
                && especialidad == null
                && estado == null
                && fechaInicio == null
                && horaBusqueda == null
                && nombreBusqueda == null
                && tipoDiagnostico == null
                && !esRankingMedicos
                && !esBusquedaDisponibilidad;
    }
}

