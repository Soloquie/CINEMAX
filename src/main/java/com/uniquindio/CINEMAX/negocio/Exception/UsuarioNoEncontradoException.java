package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un usuario requerido no existe en el sistema CINEMAX. */
public class UsuarioNoEncontradoException extends NotFoundDomainException {

    public UsuarioNoEncontradoException() {
        super("USUARIO_NO_ENCONTRADO", "El usuario no existe.");
    }

    public UsuarioNoEncontradoException(String message) {
        super("USUARIO_NO_ENCONTRADO", message);
    }
}