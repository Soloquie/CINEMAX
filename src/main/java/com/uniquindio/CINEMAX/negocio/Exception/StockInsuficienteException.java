package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que no existe stock suficiente para completar una operación en el sistema CINEMAX. */
public class StockInsuficienteException extends ConflictDomainException {

    public StockInsuficienteException() {
        super("STOCK_INSUFICIENTE", "No hay stock suficiente para completar la operación.");
    }

    public StockInsuficienteException(String message) {
        super("STOCK_INSUFICIENTE", message);
    }
}