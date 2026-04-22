package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un correo electrónico ya se encuentra registrado en el sistema CINEMAX. */
public class EmailYaRegistradoException extends ConflictDomainException {

    public EmailYaRegistradoException() {
        super("EMAIL_YA_REGISTRADO", "Ya existe un usuario registrado con ese email.");
    }

    public EmailYaRegistradoException(String message) {
        super("EMAIL_YA_REGISTRADO", message);
    }
}