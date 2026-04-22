package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un usuario no está habilitado para realizar una operación en el sistema CINEMAX. */
public class UsuarioNoHabilitadoException extends UnauthorizedDomainException {

    public UsuarioNoHabilitadoException() {
        super("USUARIO_NO_HABILITADO", "El usuario no está habilitado para realizar esta operación.");
    }

    public UsuarioNoHabilitadoException(String message) {
        super("USUARIO_NO_HABILITADO", message);
    }
}