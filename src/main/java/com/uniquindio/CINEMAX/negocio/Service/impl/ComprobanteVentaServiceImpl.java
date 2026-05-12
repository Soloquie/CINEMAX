package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.uniquindio.CINEMAX.negocio.DTO.BoletaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.VentaProductoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.VentaResponseDTO;
import com.uniquindio.CINEMAX.negocio.Exception.PagoInvalidoException;
import com.uniquindio.CINEMAX.negocio.Service.ComprobanteVentaService;
import com.uniquindio.CINEMAX.negocio.Service.VentaService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ComprobanteVentaServiceImpl implements ComprobanteVentaService {

    private final VentaService ventaService;

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${app.mail.from-name:CINEMAX}")
    private String mailFromName;

    @Override
    public byte[] generarPdfComprobante(Long ventaId, String userEmail) {
        try {
            VentaResponseDTO venta = ventaService.detalleVenta(ventaId, userEmail);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font subtitulo = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 11);

            Paragraph encabezado = new Paragraph("COMPROBANTE DE COMPRA - CINEMAX", titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            encabezado.setSpacingAfter(20);
            document.add(encabezado);

            document.add(new Paragraph("Código de venta: " + venta.codigoVenta(), normal));
            document.add(new Paragraph("Estado de venta: " + venta.estado(), normal));
            document.add(new Paragraph("Total pagado: $" + venta.total(), normal));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Boletas", subtitulo));
            document.add(Chunk.NEWLINE);

            PdfPTable tablaBoletas = new PdfPTable(5);
            tablaBoletas.setWidthPercentage(100);

            agregarCelda(tablaBoletas, "Código");
            agregarCelda(tablaBoletas, "Película");
            agregarCelda(tablaBoletas, "Cine");
            agregarCelda(tablaBoletas, "Asiento");
            agregarCelda(tablaBoletas, "Precio");

            for (BoletaResponseDTO b : venta.boletas()) {
                agregarCelda(tablaBoletas, b.codigo());
                agregarCelda(tablaBoletas, b.pelicula());
                agregarCelda(tablaBoletas, b.cine());
                agregarCelda(tablaBoletas, b.fila() + b.numero());
                agregarCelda(tablaBoletas, "$" + b.precio());
            }

            document.add(tablaBoletas);
            document.add(Chunk.NEWLINE);

            if (venta.productos() != null && !venta.productos().isEmpty()) {
                document.add(new Paragraph("Productos de confitería", subtitulo));
                document.add(Chunk.NEWLINE);

                PdfPTable tablaProductos = new PdfPTable(4);
                tablaProductos.setWidthPercentage(100);

                agregarCelda(tablaProductos, "Producto");
                agregarCelda(tablaProductos, "Cantidad");
                agregarCelda(tablaProductos, "Precio unitario");
                agregarCelda(tablaProductos, "Subtotal");

                for (VentaProductoResponseDTO p : venta.productos()) {
                    agregarCelda(tablaProductos, p.nombre());
                    agregarCelda(tablaProductos, String.valueOf(p.cantidad()));
                    agregarCelda(tablaProductos, "$" + p.precioUnitario());
                    agregarCelda(tablaProductos, "$" + p.subtotal());
                }

                document.add(tablaProductos);
            }

            document.add(Chunk.NEWLINE);

            Paragraph pie = new Paragraph("Gracias por comprar en CINEMAX.", normal);
            pie.setAlignment(Element.ALIGN_CENTER);
            document.add(pie);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new PagoInvalidoException("No se pudo generar el comprobante PDF: " + e.getMessage());
        }
    }

    @Override
    public void enviarComprobantePorCorreo(Long ventaId, String userEmail) {
        try {
            VentaResponseDTO venta = ventaService.detalleVenta(ventaId, userEmail);
            byte[] pdf = generarPdfComprobante(ventaId, userEmail);

            Email from = new Email(mailFrom, mailFromName);
            Email to = new Email(userEmail);

            String subject = "Comprobante de compra CINEMAX - " + venta.codigoVenta();

            Content content = new Content(
                    "text/plain",
                    """
                    Hola,

                    Tu compra en CINEMAX fue confirmada correctamente.
                    Adjuntamos el comprobante en PDF.

                    Gracias por comprar con nosotros.
                    """
            );

            Mail mail = new Mail(from, subject, to, content);

            Attachments attachment = new Attachments();
            attachment.setContent(Base64.getEncoder().encodeToString(pdf));
            attachment.setType("application/pdf");
            attachment.setFilename("comprobante-cinemax-" + venta.codigoVenta() + ".pdf");
            attachment.setDisposition("attachment");

            mail.addAttachments(attachment);

            SendGrid sendGrid = new SendGrid(sendGridApiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new PagoInvalidoException(
                        "SendGrid no pudo enviar el correo. Código: " + response.getStatusCode()
                );
            }

        } catch (PagoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new PagoInvalidoException("No se pudo enviar el comprobante por correo: " + e.getMessage());
        }
    }

    private void agregarCelda(PdfPTable tabla, String texto) {
        tabla.addCell(texto == null ? "" : texto);
    }
}