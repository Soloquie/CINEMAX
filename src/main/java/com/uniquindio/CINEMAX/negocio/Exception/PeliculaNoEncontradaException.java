package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que una película requerida no existe en el sistema CINEMAX. */
public class PeliculaNoEncontradaException extends NotFoundDomainException {

    public PeliculaNoEncontradaException() {
        super("PELICULA_NO_ENCONTRADA", "La película no existe.");
    }

    public PeliculaNoEncontradaException(String message) {
        super("PELICULA_NO_ENCONTRADA", message);
    }
}