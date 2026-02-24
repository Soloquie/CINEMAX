package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.negocio.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerificationEmail(String toEmail, String toName, String verificationLink) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject("Verifica tu cuenta - CINEMAX");
        msg.setText(
                "Hola " + toName + ",\n\n" +
                        "Para verificar tu cuenta, abre este enlace:\n" +
                        verificationLink + "\n\n" +
                        "Si no fuiste tú, ignora este correo."
        );
        mailSender.send(msg);
    }
}