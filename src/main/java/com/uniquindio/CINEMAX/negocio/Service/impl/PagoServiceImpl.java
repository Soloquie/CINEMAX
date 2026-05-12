package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.mercadopago.resources.payment.Payment;
import com.uniquindio.CINEMAX.config.MercadoPagoProperties;
import com.uniquindio.CINEMAX.config.StripeProperties;
import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.CarritoInvalidoException;
import com.uniquindio.CINEMAX.negocio.Exception.ConfiteriaNoPermitidaSinBoletaException;
import com.uniquindio.CINEMAX.negocio.Exception.PagoInvalidoException;
import com.uniquindio.CINEMAX.negocio.Exception.PagoNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Exception.UsuarioNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Service.MercadoPagoService;
import com.uniquindio.CINEMAX.negocio.Service.PagoService;
import com.uniquindio.CINEMAX.negocio.Service.StripeService;
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
 * Esta clase crea pagos pendientes desde el carrito del usuario, genera checkout con Mercado Pago o Stripe
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

    private final StripeService stripeService;
    private final StripeProperties stripeProperties;

    private final VentaService ventaService;

    /**
     * Crea un checkout de pago a partir del carrito activo del usuario autenticado.
     * El total se calcula en backend para evitar manipulación desde el frontend.
     * El proveedor puede ser MERCADO_PAGO o STRIPE.
     */
    @Override
    public CheckoutPagoResponseDTO crearCheckout(String userEmail, CrearCheckoutPagoRequestDTO request) {
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

        ProveedorPago proveedor = resolverProveedor(request);

        BigDecimal total = calcularTotal(items);

        Long montoCentavos = total
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        PagoEntity pago = PagoEntity.builder()
                .usuario(usuario)
                .carrito(carrito)
                .referencia(generarReferencia(usuario.getId()))
                .proveedor(proveedor)
                .monto(total)
                .montoCentavos(montoCentavos)
                .moneda("COP")
                .estado(EstadoPago.PENDIENTE)
                .build();

        PagoEntity saved = pagoRepository.save(pago);

        return switch (proveedor) {
            case MERCADO_PAGO -> crearCheckoutMercadoPago(saved);
            case STRIPE -> crearCheckoutStripe(saved);
        };
    }

    /**
     * Crea la preferencia de Mercado Pago y devuelve la URL de redirección al frontend.
     */
    private CheckoutPagoResponseDTO crearCheckoutMercadoPago(PagoEntity pago) {
        MercadoPagoPreferenceDataDTO preference = mercadoPagoService.crearPreferencia(pago);

        pago.setProviderPreferenceId(preference.preferenceId());
        pagoRepository.save(pago);

        String initPoint = resolverInitPointMercadoPago(preference);

        return new CheckoutPagoResponseDTO(
                pago.getReferencia(),
                pago.getProveedor().name(),
                pago.getMontoCentavos(),
                pago.getMoneda(),
                preference.preferenceId(),
                initPoint,
                mercadoPagoProperties.getPublicKey()
        );
    }

    /**
     * Crea la sesión de Stripe Checkout y devuelve la URL de redirección al frontend.
     */
    private CheckoutPagoResponseDTO crearCheckoutStripe(PagoEntity pago) {
        StripeCheckoutDataDTO session = stripeService.crearSesionCheckout(pago);

        pago.setProviderPreferenceId(session.sessionId());
        pagoRepository.save(pago);

        return new CheckoutPagoResponseDTO(
                pago.getReferencia(),
                pago.getProveedor().name(),
                pago.getMontoCentavos(),
                pago.getMoneda(),
                session.sessionId(),
                session.url(),
                stripeProperties.getPublicKey()
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
        String eventType = extraerEventTypeMercadoPago(body, params);

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

        if (esPagoRechazadoMercadoPago(normalizedStatus)) {
            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setMensajeError("Pago rechazado o cancelado por Mercado Pago.");
            pagoRepository.save(pago);
            return;
        }

        pago.setEstado(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);
    }

    /**
     * Procesa un evento webhook recibido desde Stripe.
     * El evento principal para confirmar la compra es checkout.session.completed.
     */
    @Override
    public void procesarWebhookStripe(String payload, String signature) {
        StripeWebhookDataDTO webhookData = stripeService.procesarWebhook(payload, signature);

        String eventType = webhookData.eventType() == null
                ? ""
                : webhookData.eventType().trim().toLowerCase();

        String paymentStatus = webhookData.paymentStatus() == null
                ? ""
                : webhookData.paymentStatus().trim().toLowerCase();

        /*
         * Eventos secundarios de Stripe:
         * charge.succeeded, payment_intent.succeeded, payment_intent.created, charge.updated, etc.
         * Se reciben correctamente, pero no se procesan en CINEMAX.
         */
        if (!"checkout.session.completed".equals(eventType)
                && !"checkout.session.expired".equals(eventType)) {
            return;
        }

        PagoEntity pago = buscarPagoStripe(webhookData);

        if (pago == null) {
            return;
        }

        pagoEventoRepository.save(PagoEventoEntity.builder()
                .pago(pago)
                .proveedor(ProveedorPago.STRIPE.name())
                .eventType(webhookData.eventType())
                .transactionId(webhookData.sessionId())
                .estadoTransaccion(webhookData.paymentStatus())
                .payloadJson(payload)
                .build());

        if (webhookData.sessionId() != null && pago.getProviderPreferenceId() == null) {
            pago.setProviderPreferenceId(webhookData.sessionId());
        }

        if (webhookData.paymentIntentId() != null) {
            pago.setProviderPaymentId(webhookData.paymentIntentId());
        }

        if ("checkout.session.completed".equals(eventType)) {

            if (!"paid".equals(paymentStatus) && !"complete".equals(paymentStatus)) {
                return;
            }

            procesarPagoAprobado(pago);
            return;
        }

        if ("checkout.session.expired".equals(eventType)) {
            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setMensajeError("La sesión de pago de Stripe expiró.");
            pagoRepository.save(pago);
        }
    }

    /**
     * Busca el pago asociado al webhook de Stripe usando primero la referencia interna y luego el ID de sesión.
     */
    private PagoEntity buscarPagoStripe(StripeWebhookDataDTO webhookData) {
        if (webhookData.referencia() != null && !webhookData.referencia().isBlank()) {
            return pagoRepository.findByReferencia(webhookData.referencia()).orElse(null);
        }

        if (webhookData.sessionId() != null && !webhookData.sessionId().isBlank()) {
            return pagoRepository.findByProviderPreferenceId(webhookData.sessionId()).orElse(null);
        }

        return null;
    }

    /**
     * Procesa un pago aprobado evitando duplicar ventas si el proveedor reintenta el webhook.
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

            pago.setEstado(EstadoPago.CONFIRMADO);
            pago.setConfirmadoEn(LocalDateTime.now());
            pago.setMensajeError(null);
            pagoRepository.save(pago);

        } catch (RuntimeException ex) {
            pago.setEstado(EstadoPago.FALLIDO_CONFIRMACION);
            pago.setMensajeError(ex.getMessage());
            pagoRepository.save(pago);
        }
    }

    private ProveedorPago resolverProveedor(CrearCheckoutPagoRequestDTO request) {
        if (request == null || request.proveedor() == null || request.proveedor().isBlank()) {
            return ProveedorPago.MERCADO_PAGO;
        }

        try {
            return ProveedorPago.valueOf(request.proveedor().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new PagoInvalidoException("Proveedor de pago no soportado: " + request.proveedor());
        }
    }

    private boolean esPagoRechazadoMercadoPago(String status) {
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

    private String resolverInitPointMercadoPago(MercadoPagoPreferenceDataDTO preference) {
        boolean test = "test".equalsIgnoreCase(mercadoPagoProperties.getEnvironment())
                || "sandbox".equalsIgnoreCase(mercadoPagoProperties.getEnvironment());

        if (test && preference.sandboxInitPoint() != null && !preference.sandboxInitPoint().isBlank()) {
            return preference.sandboxInitPoint();
        }

        return preference.initPoint();
    }

    private String extraerEventTypeMercadoPago(Map<String, Object> body, Map<String, String> params) {
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