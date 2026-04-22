package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un cine existe, pero se encuentra inactivo en el sistema CINEMAX. */
public class CineInactivoException extends ConflictDomainException {

    public CineInactivoException() {
        super("CINE_INACTIVO", "El cine está inactivo.");
    }

    public CineInactivoException(String message) {
        super("CINE_INACTIVO", message);
    }
}