package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions;

public class FusekiConnectionException extends SemanticException {
    public FusekiConnectionException(String detail) {
        super("Error de comunicación con el servidor Fuseki: " + detail);
    }
}