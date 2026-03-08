package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions;

public class ParserException extends SemanticException {
    public ParserException(String textoOriginal) {
        super("No se pudo extraer información válida de la frase: '" + textoOriginal + "'. Pruebe con DNI o Especialidad.");
    }
}