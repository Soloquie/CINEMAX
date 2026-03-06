package com.uniquindio.CINEMAX.excepciones;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Validaciones de DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleDtoValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Campos inválidos", request.getRequestURI(), fieldErrors);
    }

    // 400 - Validaciones en path/query params (si usas @Validated + constraints)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), request.getRequestURI(), null);
    }

    // 400 - Errores de negocio simples (los que tiras con IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request.getRequestURI(), null);
    }

    // 409 - Conflicto típico (por ejemplo email ya existe) si lo usas con IllegalStateException
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), request.getRequestURI(), null);
    }

    // 500 - Cualquier error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        // No devuelvas stacktrace al cliente; solo mensaje genérico
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error inesperado. Intenta de nuevo.", request.getRequestURI(), null);
    }

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            String code,
            String message,
            String path,
            Object details
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", code);
        body.put("message", message);
        body.put("path", path);
        if (details != null) body.put("details", details);

        return ResponseEntity.status(status).body(body);
    }
}