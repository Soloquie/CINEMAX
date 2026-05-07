package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que una venta requerida no existe en el sistema CINEMAX. */
public class VentaNoEncontradaException extends NotFoundDomainException {

    public VentaNoEncontradaException() {
        super("VENTA_NO_ENCONTRADA", "La venta no existe.");
    }

    public VentaNoEncontradaException(String message) {
        super("VENTA_NO_ENCONTRADA", message);
    }
}