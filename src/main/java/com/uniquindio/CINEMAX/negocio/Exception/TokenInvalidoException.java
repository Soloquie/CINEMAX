package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un token no es válido para la operación solicitada en el sistema CINEMAX. */
public class TokenInvalidoException extends ValidationDomainException {

    public TokenInvalidoException() {
        super("TOKEN_INVALIDO", "El token no es válido.");
    }

    public TokenInvalidoException(String message) {
        super("TOKEN_INVALIDO", message);
    }
}