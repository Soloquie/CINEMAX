package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que una reserva o retención temporal ha expirado en el sistema CINEMAX. */
public class ReservaExpiradaException extends ConflictDomainException {

    public ReservaExpiradaException() {
        super("RESERVA_EXPIRADA", "La reserva temporal ha expirado.");
    }

    public ReservaExpiradaException(String message) {
        super("RESERVA_EXPIRADA", message);
    }
}