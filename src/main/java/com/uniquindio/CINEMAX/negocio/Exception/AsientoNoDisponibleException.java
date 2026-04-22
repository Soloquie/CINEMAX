package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un asiento no está disponible para reserva o compra en el sistema CINEMAX. */
public class AsientoNoDisponibleException extends ConflictDomainException {

    public AsientoNoDisponibleException() {
        super("ASIENTO_NO_DISPONIBLE", "El asiento no está disponible.");
    }

    public AsientoNoDisponibleException(String message) {
        super("ASIENTO_NO_DISPONIBLE", message);
    }
}