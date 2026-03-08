package org.springcloud.nova.ing.atencion.medica.msvc.web.semantica.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ParserException.class)
    public ResponseEntity<ErrorResponse> handleParserException(ParserException ex, HttpServletRequest request) {
        log.warn("ParserException: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de Interpretación", ex.getMessage(), request);
    }

    @ExceptionHandler(FusekiConnectionException.class)
    public ResponseEntity<ErrorResponse> handleFusekiException(FusekiConnectionException ex, HttpServletRequest request) {
        log.error("FusekiConnectionException: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Error de Infraestructura Semántica", ex.getMessage(), request);
    }

    @ExceptionHandler(SemanticException.class)
    public ResponseEntity<ErrorResponse> handleSemanticException(SemanticException ex, HttpServletRequest request) {
        log.error("SemanticException: {}", ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error Semántico", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Excepción no controlada en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno",
                "Error inesperado: " + ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, status);
    }
}