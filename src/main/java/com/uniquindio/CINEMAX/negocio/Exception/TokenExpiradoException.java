package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un token ha expirado en el sistema CINEMAX. */
public class TokenExpiradoException extends ValidationDomainException {

    public TokenExpiradoException() {
        super("TOKEN_EXPIRADO", "El token ha expirado.");
    }

    public TokenExpiradoException(String message) {
        super("TOKEN_EXPIRADO", message);
    }
}