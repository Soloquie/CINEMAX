package com.uniquindio.CINEMAX.negocio.Service;
/**
 * Interfaz de servicio para gestionar el envío de correos electrónicos en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para enviar correos de verificación y restablecimiento de contraseña a los usuarios.
 */
public interface EmailService {
    void sendVerificationEmail(String toEmail, String toName, String verificationLink);
    void sendPasswordResetEmail(String toEmail, String toName, String resetLink);
}