package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un cine requerido no existe en el sistema CINEMAX. */
public class CineNoEncontradoException extends NotFoundDomainException {

    public CineNoEncontradoException() {
        super("CINE_NO_ENCONTRADO", "El cine no existe.");
    }

    public CineNoEncontradoException(String message) {
        super("CINE_NO_ENCONTRADO", message);
    }
}