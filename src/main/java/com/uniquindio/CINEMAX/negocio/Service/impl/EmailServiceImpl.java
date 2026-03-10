package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import com.uniquindio.CINEMAX.negocio.Service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
/**
 * Implementación de la interfaz EmailService para gestionar el envío de correos electrónicos en el sistema CINEMAX.
 * Esta clase utiliza la biblioteca SendGrid para interactuar con el servicio de envío de correos y
 * realizar las operaciones necesarias.
 */
@Service
public class EmailServiceImpl implements EmailService {
    // --- Configuraciones desde application.properties ---
    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String from;

    @Value("${spring.application.name:CINEMAX}")
    private String appName;

    @Value("${app.verification.token-minutes:1440}")
    private long tokenMinutes;

    /**
     * Implementación del método para enviar un correo de verificación. Este método recibe el correo electrónico del destinatario,
     * su nombre y el enlace de verificación, y utiliza SendGrid para enviar un correo
     * @param toEmail Correo electrónico del destinatario.
     * @param toName Nombre del destinatario, utilizado para personalizar el saludo en el correo.
     * @param verificationLink Enlace que el destinatario debe abrir para verificar su cuenta.
     * Este enlace generalmente contiene un token de verificación que expira después de un tiempo determinado.
     */
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
        // --- HTML enriquecido ---
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
        // --- Enviar correo usando SendGrid ---
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
    /**
     * Implementación del método para enviar un correo de restablecimiento de contraseña.
     * Este método recibe el correo electrónico del destinatario,
     * su nombre y el enlace de restablecimiento, y utiliza SendGrid para enviar un correo
     * @param toEmail Correo electrónico del destinatario.
     * @param toName Nombre del destinatario, utilizado para personalizar el saludo en el correo.
     * @param resetLink Enlace que el destinatario debe abrir para restablecer su contraseña.
     * Este enlace generalmente contiene un token de restablecimiento que expira después de un tiempo determinado.
     */
    @Override
    public void sendPasswordResetEmail(String toEmail, String toName, String resetLink) {

        String text = """
            Hola %s,

            Recibimos una solicitud para restablecer tu contraseña en %s.
            Abre este enlace para continuar:
            %s

            Si tú no solicitaste este cambio, ignora este correo.
            """.formatted(toName, appName, resetLink);
        // --- HTML enriquecido ---
        String html = """
            <!doctype html>
            <html lang="es">
            <head><meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/></head>
            <body style="margin:0;background:#f6f7fb;font-family:Arial,Helvetica,sans-serif;">
              <div style="max-width:600px;margin:0 auto;padding:24px;">
                <div style="background:#111827;color:#fff;border-radius:14px;padding:20px 22px;">
                  <div style="font-size:18px;font-weight:700;">%s</div>
                  <div style="margin-top:6px;font-size:13px;opacity:.9;">Restablecimiento de contraseña</div>
                </div>

                <div style="background:#fff;border-radius:14px;padding:22px;margin-top:14px;box-shadow:0 6px 20px rgba(17,24,39,.08);">
                  <p style="margin:0 0 10px 0;font-size:16px;color:#111827;">Hola <b>%s</b>,</p>
                  <p style="margin:0 0 14px 0;font-size:14px;color:#374151;line-height:1.5;">
                    Recibimos una solicitud para restablecer tu contraseña. Si fuiste tú, continúa con el botón:
                  </p>

                  <div style="margin:18px 0;">
                    <a href="%s"
                       style="display:inline-block;background:#2563EB;color:#fff;text-decoration:none;
                              padding:12px 18px;border-radius:10px;font-weight:700;font-size:14px;">
                      Restablecer contraseña
                    </a>
                  </div>

                  <p style="margin:0;font-size:13px;color:#6B7280;line-height:1.5;">
                    Si no solicitaste esto, ignora este correo.
                    <br/><br/>
                    Enlace alternativo:
                    <br/>
                    <a href="%s" style="color:#2563EB;word-break:break-all;">%s</a>
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(appName, toName, resetLink, resetLink, resetLink);

        sendViaSendGrid(toEmail, "Restablecer contraseña - " + appName, text, html);
    }

    /**
     * Método privado para enviar un correo utilizando SendGrid.
     * Este método se encarga de construir el correo con el formato adecuado y manejar la respuesta del servicio de envío.
     * @param toEmail Correo electrónico del destinatario.
     * @param subject Asunto del correo.
     * @param text Contenido del correo en formato texto plano, utilizado como fallback para clientes de correo que no soportan HTML.
     * @param html Contenido del correo en formato HTML, utilizado para clientes de correo que soportan HTML y
     * permite un diseño más enriquecido.
     */
    private void sendViaSendGrid(String toEmail, String subject, String text, String html) {
        try {
            Email fromEmail = new Email(from);
            Email to = new Email(toEmail);

            Mail mail = new Mail();
            mail.setFrom(fromEmail);
            mail.setSubject(subject);
            // Es importante agregar ambos contenidos (html + text) para
            // asegurar la compatibilidad con todos los clientes de correo
            Personalization personalization = new Personalization();
            personalization.addTo(to);
            mail.addPersonalization(personalization);

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