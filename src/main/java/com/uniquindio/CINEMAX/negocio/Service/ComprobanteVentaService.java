package com.uniquindio.CINEMAX.negocio.Service;

public interface ComprobanteVentaService {

    byte[] generarPdfComprobante(Long ventaId, String userEmail);

    void enviarComprobantePorCorreo(Long ventaId, String userEmail);
}