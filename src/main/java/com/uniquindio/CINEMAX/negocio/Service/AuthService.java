package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;
/**
 * Interfaz de servicio para gestionar la autenticación y el registro de usuarios en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para registrar nuevos usuarios, iniciar sesión, verificar el correo electrónico,
 * reenviar el correo de verificación, solicitar restablecimiento de contraseña y restablecer la contraseña.
 */
public interface AuthService {

    MessageResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    MessageResponseDTO verifyEmail(VerifyEmailRequestDTO request);

    MessageResponseDTO resendVerification(ResendVerificationDTO request);

    MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO request);

    MessageResponseDTO resetPassword(ResetPasswordRequestDTO request);
}