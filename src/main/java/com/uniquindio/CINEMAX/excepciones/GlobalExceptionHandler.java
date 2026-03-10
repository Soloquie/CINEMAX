package com.uniquindio.CINEMAX.excepciones;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
/**
 * Clase de manejo global de excepciones para el sistema CINEMAX.
 * Esta clase captura y maneja diferentes tipos de excepciones que pueden ocurrir en los controladores REST,
 * proporcionando respuestas HTTP adecuadas con información detallada sobre el error ocurrido.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Maneja excepciones de tipo IllegalArgumentException, que generalmente
    // indican que se ha pasado un argumento no válido a un método.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req.getRequestURI());
    }
    // Maneja excepciones de tipo IllegalStateException, que generalmente
    // indican que el estado actual del sistema no permite la operación solicitada.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> conflict(IllegalStateException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), req.getRequestURI());
    }
    // Maneja excepciones de tipo AccessDeniedException, que indican que el usuario no tiene
    // permisos para realizar la acción solicitada.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> forbidden(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "No tienes permisos para esta acción", req.getRequestURI());
    }
    // Maneja excepciones de tipo MethodArgumentNotValidException, que ocurren cuando la validación
    // de los argumentos de un método falla.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));

        Map<String,Object> body = base(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Datos inválidos", req.getRequestURI());
        body.put("fields", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    // Maneja cualquier otra excepción no capturada por los manejadores anteriores, proporcionando
    // una respuesta genérica de error interno del servidor.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> internal(Exception ex, HttpServletRequest req) {
        // Log completo en consola:
        ex.printStackTrace();

        // Un id para rastrear
        String errorId = UUID.randomUUID().toString().substring(0, 8);

        Map<String,Object> body = base(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error inesperado. ErrorId=" + errorId, req.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    // Método auxiliar para construir la respuesta HTTP con el formato estándar de error utilizado en el sistema.
    private ResponseEntity<Map<String,Object>> build(HttpStatus status, String code, String msg, String path) {
        return ResponseEntity.status(status).body(base(status, code, msg, path));
    }
    // Método auxiliar para construir el cuerpo de la respuesta de error con información detallada sobre el error ocurrido
    private Map<String,Object> base(HttpStatus status, String code, String msg, String path) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", code);
        body.put("message", msg);
        body.put("path", path);
        return body;
    }
}