package com.uniquindio.CINEMAX.negocio.Exception;

import org.springframework.http.HttpStatus;
/* Excepción base abstracta para representar errores de dominio en el sistema CINEMAX.
 * Esta clase permite asociar a cada excepción un código funcional y un estado HTTP,
 * facilitando un manejo uniforme, semántico y controlado de los errores en la API.
 */
public abstract class DomainException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    protected DomainException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}