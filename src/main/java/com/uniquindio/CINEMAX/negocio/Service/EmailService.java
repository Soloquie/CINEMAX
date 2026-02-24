package com.uniquindio.CINEMAX.negocio.Service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String toName, String verificationLink);
}