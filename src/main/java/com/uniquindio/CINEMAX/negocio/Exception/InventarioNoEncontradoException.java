package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un inventario requerido no existe en el sistema CINEMAX. */
public class InventarioNoEncontradoException extends NotFoundDomainException {

    public InventarioNoEncontradoException() {
        super("INVENTARIO_NO_ENCONTRADO", "El inventario no existe.");
    }

    public InventarioNoEncontradoException(String message) {
        super("INVENTARIO_NO_ENCONTRADO", message);
    }
}