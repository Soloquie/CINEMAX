package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar credenciales inválidas durante el proceso de autenticación en el sistema CINEMAX. */
public class CredencialesInvalidasException extends UnauthorizedDomainException {

    public CredencialesInvalidasException() {
        super("CREDENCIALES_INVALIDAS", "Credenciales inválidas.");
    }

    public CredencialesInvalidasException(String message) {
        super("CREDENCIALES_INVALIDAS", message);
    }
}