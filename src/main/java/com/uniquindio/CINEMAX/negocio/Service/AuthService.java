package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;

public interface AuthService {

    MessageResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    MessageResponseDTO verifyEmail(VerifyEmailRequestDTO request);

    MessageResponseDTO resendVerification(ResendVerificationDTO request);
}