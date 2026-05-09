package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.mercadopago.resources.payment.Payment;
import com.uniquindio.CINEMAX.config.MercadoPagoProperties;
import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoCheckoutResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoPreferenceDataDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PagoEstadoResponseDTO;
import com.uniquindio.CINEMAX.negocio.Exception.CarritoInvalidoException;
import com.uniquindio.CINEMAX.negocio.Exception.ConfiteriaNoPermitidaSinBoletaException;
import com.uniquindio.CINEMAX.negocio.Exception.PagoNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Exception.UsuarioNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Service.MercadoPagoService;
import com.uniquindio.CINEMAX.negocio.Service.PagoService;
import com.uniquindio.CINEMAX.negocio.Service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación de la interfaz PagoService para gestionar el inicio y seguimiento de pagos en CINEMAX.
 * Esta clase crea pagos pendientes desde el carrito del usuario, genera la preferencia de Mercado Pago
 * y procesa los eventos webhook para confirmar la venta de forma transaccional.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PagoRepository pagoRepository;
    private final PagoEventoRepository pagoEventoRepository;
    private final VentaRepository ventaRepository;
    private final MercadoPagoService mercadoPagoService;
    private final MercadoPagoProperties mercadoPagoProperties;
    private final VentaService ventaService;

    /**
     * Crea un checkout de pago a partir del carrito activo del usuario autenticado.
     * El total se calcula en backend para evitar manipulación desde el frontend.
     */
    @Override
    public MercadoPagoCheckoutResponseDTO crearCheckout(String userEmail) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(UsuarioNoEncontradoException::new);

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new CarritoInvalidoException("No existe un carrito activo para iniciar el pago."));

        List<CarritoItemEntity> items = carritoItemRepository.findItemsWithDetails(carrito.getId());

        if (items.isEmpty()) {
            throw new CarritoInvalidoException("El carrito está vacío.");
        }

        boolean tieneAsientos = items.stream()
                .anyMatch(i -> i.getTipo() == TipoCarritoItem.ASIENTO);

        if (!tieneAsientos) {
            throw new ConfiteriaNoPermitidaSinBoletaException(
                    "Debes tener al menos una boleta en el carrito antes de pagar."
            );
        }

        validarHoldsVigentes(items);

        BigDecimal total = calcularTotal(items);

        Long montoCentavos = total
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        PagoEntity pago = PagoEntity.builder()
                .usuario(usuario)
                .carrito(carrito)
                .referencia(generarReferencia(usuario.getId()))
                .proveedor(ProveedorPago.MERCADO_PAGO)
                .monto(total)
                .montoCentavos(montoCentavos)
                .moneda("COP")
                .estado(EstadoPago.PENDIENTE)
                .build();

        PagoEntity saved = pagoRepository.save(pago);

        MercadoPagoPreferenceDataDTO preference = mercadoPagoService.crearPreferencia(saved);

        saved.setProviderPreferenceId(preference.preferenceId());
        pagoRepository.save(saved);

        String initPoint = resolverInitPoint(preference);

        return new MercadoPagoCheckoutResponseDTO(
                saved.getReferencia(),
                preference.preferenceId(),
                initPoint,
                mercadoPagoProperties.getPublicKey()
        );
    }

    /**
     * Consulta el estado de un pago y su venta asociada para el usuario autenticado.
     */
    @Override
    @Transactional(readOnly = true)
    public PagoEstadoResponseDTO consultarEstado(String referencia, String userEmail) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(UsuarioNoEncontradoException::new);

        PagoEntity pago = pagoRepository.findByReferencia(referencia)
                .orElseThrow(PagoNoEncontradoException::new);

        if (!pago.getUsuario().getId().equals(usuario.getId())) {
            throw new PagoNoEncontradoException("No existe un pago asociado al usuario autenticado.");
        }

        var ventaOpt = ventaRepository.findByPagoId(pago.getId());

        return new PagoEstadoResponseDTO(
                pago.getReferencia(),
                pago.getEstado().name(),
                ventaOpt.map(v -> v.getEstado().name()).orElse(null),
                ventaOpt.map(VentaEntity::getId).orElse(null),
                ventaOpt.map(VentaEntity::getCodigo).orElse(null),
                pago.getMensajeError()
        );
    }

    /**
     * Procesa un evento webhook recibido desde Mercado Pago.
     * Si el pago fue aprobado, dispara la confirmación transaccional de la venta.
     */
    @Override
    public void procesarWebhookMercadoPago(Map<String, Object> body, Map<String, String> params) {
        String paymentId = mercadoPagoService.extraerPaymentId(body, params);
        String eventType = extraerEventType(body, params);

        if (paymentId == null || paymentId.isBlank()) {
            pagoEventoRepository.save(PagoEventoEntity.builder()
                    .pago(null)
                    .proveedor(ProveedorPago.MERCADO_PAGO.name())
                    .eventType(eventType)
                    .transactionId(null)
                    .estadoTransaccion(null)
                    .payloadJson(mercadoPagoService.payloadToJson(body, params))
                    .build());
            return;
        }

        Payment payment = mercadoPagoService.obtenerPago(paymentId);

        String estadoPagoExterno = payment.getStatus();
        String referencia = payment.getExternalReference();

        PagoEntity pago = null;

        if (referencia != null && !referencia.isBlank()) {
            pago = pagoRepository.findByReferencia(referencia).orElse(null);
        }

        pagoEventoRepository.save(PagoEventoEntity.builder()
                .pago(pago)
                .proveedor(ProveedorPago.MERCADO_PAGO.name())
                .eventType(eventType)
                .transactionId(paymentId)
                .estadoTransaccion(estadoPagoExterno)
                .payloadJson(mercadoPagoService.payloadToJson(body, params))
                .build());

        if (pago == null) {
            return;
        }

        pago.setProviderPaymentId(paymentId);

        String normalizedStatus = estadoPagoExterno == null ? "" : estadoPagoExterno.trim().toLowerCase();

        if ("approved".equals(normalizedStatus)) {
            procesarPagoAprobado(pago);
            return;
        }

        if (esPagoRechazado(normalizedStatus)) {
            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setMensajeError("Pago rechazado o cancelado por Mercado Pago.");
            pagoRepository.save(pago);
            return;
        }

        pago.setEstado(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);
    }

    /**
     * Procesa un pago aprobado evitando duplicar ventas si Mercado Pago reintenta el webhook.
     */
    private void procesarPagoAprobado(PagoEntity pago) {
        if (pago.getEstado() == EstadoPago.CONFIRMADO) {
            pagoRepository.save(pago);
            return;
        }

        if (ventaRepository.findByPagoId(pago.getId()).isPresent()) {
            pago.setEstado(EstadoPago.CONFIRMADO);
            pago.setConfirmadoEn(
                    pago.getConfirmadoEn() == null ? LocalDateTime.now() : pago.getConfirmadoEn()
            );
            pago.setMensajeError(null);
            pagoRepository.save(pago);
            return;
        }

        pago.setEstado(EstadoPago.APROBADO);
        pagoRepository.save(pago);

        try {
            ventaService.confirmarVentaDesdePago(pago.getReferencia());
        } catch (RuntimeException ex) {
            pago.setEstado(EstadoPago.FALLIDO_CONFIRMACION);
            pago.setMensajeError(ex.getMessage());
            pagoRepository.save(pago);
        }
    }

    private boolean esPagoRechazado(String status) {
        return "rejected".equals(status)
                || "cancelled".equals(status)
                || "refunded".equals(status)
                || "charged_back".equals(status);
    }

    private void validarHoldsVigentes(List<CarritoItemEntity> items) {
        LocalDateTime now = LocalDateTime.now();

        for (CarritoItemEntity item : items) {
            if (item.getTipo() == TipoCarritoItem.ASIENTO && item.getFuncionAsiento() != null) {
                LocalDateTime expira = item.getFuncionAsiento().getRetencionExpira();

                if (expira == null || !expira.isAfter(now)) {
                    throw new CarritoInvalidoException("La reserva de uno o más asientos ha expirado.");
                }

                if (item.getFuncionAsiento().getEstado() == EstadoFuncionAsiento.VENDIDO) {
                    throw new CarritoInvalidoException("Uno o más asientos ya fueron vendidos.");
                }
            }
        }
    }

    private BigDecimal calcularTotal(List<CarritoItemEntity> items) {
        return items.stream()
                .map(item -> {
                    int cantidad = item.getCantidad() == null ? 1 : item.getCantidad();
                    return item.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generarReferencia(Long usuarioId) {
        return "CINEMAX-" + usuarioId + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private String resolverInitPoint(MercadoPagoPreferenceDataDTO preference) {
        boolean test = "test".equalsIgnoreCase(mercadoPagoProperties.getEnvironment())
                || "sandbox".equalsIgnoreCase(mercadoPagoProperties.getEnvironment());

        if (test && preference.sandboxInitPoint() != null && !preference.sandboxInitPoint().isBlank()) {
            return preference.sandboxInitPoint();
        }

        return preference.initPoint();
    }

    private String extraerEventType(Map<String, Object> body, Map<String, String> params) {
        if (body != null) {
            Object type = body.get("type");
            if (type != null) return String.valueOf(type);

            Object topic = body.get("topic");
            if (topic != null) return String.valueOf(topic);

            Object action = body.get("action");
            if (action != null) return String.valueOf(action);
        }

        if (params != null) {
            if (params.get("type") != null) return params.get("type");
            if (params.get("topic") != null) return params.get("topic");
            if (params.get("action") != null) return params.get("action");
        }

        return "mercado_pago_webhook";
    }
}