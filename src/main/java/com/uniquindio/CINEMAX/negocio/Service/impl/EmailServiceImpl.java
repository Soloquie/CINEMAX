package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import com.uniquindio.CINEMAX.negocio.Service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String from;

    @Value("${spring.application.name:CINEMAX}")
    private String appName;

    @Value("${app.verification.token-minutes:1440}")
    private long tokenMinutes;

    @Override
    public void sendVerificationEmail(String toEmail, String toName, String verificationLink) {

        // --- Texto plano (fallback) ---
        String text = """
                Hola %s,

                Para verificar tu cuenta en %s, abre este enlace:
                %s

                Este enlace expira en %d minutos.

                Si tú no creaste esta cuenta, ignora este correo.
                """.formatted(toName, appName, verificationLink, tokenMinutes);

        // --- HTML “más profesional” ---
        String html = """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="utf-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1"/>
                </head>
                <body style="margin:0;background:#f6f7fb;font-family:Arial,Helvetica,sans-serif;">
                  <div style="max-width:600px;margin:0 auto;padding:24px;">
                    <div style="background:#111827;color:#fff;border-radius:14px;padding:20px 22px;">
                      <div style="font-size:18px;font-weight:700;letter-spacing:.3px;">%s</div>
                      <div style="margin-top:6px;font-size:13px;opacity:.9;">Verificación de correo</div>
                    </div>

                    <div style="background:#fff;border-radius:14px;padding:22px;margin-top:14px;box-shadow:0 6px 20px rgba(17,24,39,.08);">
                      <p style="margin:0 0 10px 0;font-size:16px;color:#111827;">Hola <b>%s</b>,</p>

                      <p style="margin:0 0 14px 0;font-size:14px;color:#374151;line-height:1.5;">
                        Gracias por registrarte en <b>%s</b>. Para completar tu registro, por favor verifica tu correo haciendo clic en el botón:
                      </p>

                      <div style="margin:18px 0 18px 0;">
                        <a href="%s"
                           style="display:inline-block;background:#E11D48;color:#fff;text-decoration:none;
                                  padding:12px 18px;border-radius:10px;font-weight:700;font-size:14px;">
                          Verificar mi correo
                        </a>
                      </div>

                      <p style="margin:0 0 10px 0;font-size:13px;color:#6B7280;line-height:1.5;">
                        Este enlace expira en <b>%d minutos</b>.
                      </p>

                      <p style="margin:0;font-size:13px;color:#6B7280;line-height:1.5;">
                        Si el botón no funciona, copia y pega este enlace en tu navegador:
                        <br/>
                        <a href="%s" style="color:#2563EB;word-break:break-all;">%s</a>
                      </p>
                    </div>

                    <div style="padding:12px 4px;color:#9CA3AF;font-size:12px;line-height:1.4;">
                      Si tú no solicitaste este registro, puedes ignorar este correo.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(appName, toName, appName, verificationLink, tokenMinutes, verificationLink, verificationLink);

        try {
            Email fromEmail = new Email(from);
            Email to = new Email(toEmail);
            String subject = "Verifica tu cuenta - " + appName;

            Mail mail = new Mail();
            mail.setFrom(fromEmail);
            mail.setSubject(subject);

            Personalization personalization = new Personalization();
            personalization.addTo(to);
            mail.addPersonalization(personalization);

            // Importante: agregar ambos contenidos (html + text)
            mail.addContent(new Content("text/plain", text));
            mail.addContent(new Content("text/html", html));

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                throw new IllegalStateException("SendGrid error HTTP " + response.getStatusCode() + ": " + response.getBody());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error enviando correo por SendGrid", e);
        }
    }
}