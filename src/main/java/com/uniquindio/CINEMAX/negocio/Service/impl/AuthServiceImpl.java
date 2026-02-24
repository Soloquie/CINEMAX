package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.RolDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.TokenUsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.UsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import com.uniquindio.CINEMAX.negocio.Service.AuthService;
import com.uniquindio.CINEMAX.negocio.Service.EmailService;
import com.uniquindio.CINEMAX.Seguridad.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;
    private final TokenUsuarioDAO tokenUsuarioDAO;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // Ej: http://localhost:8080/api/auth/verify-email
    @Value("${app.verification.endpoint:http://localhost:8080/api/auth/verify-email}")
    private String verificationEndpoint;

    // Ej: 24 horas
    @Value("${app.verification.token-minutes:1440}")
    private long verificationTokenMinutes;

    @Value("${app.jwt.expires-in-seconds:3600}")
    private long jwtExpiresInSeconds;

    @Override
    @Transactional
    public MessageResponseDTO register(RegisterRequestDTO request) {
        String email = normalizeEmail(request.email());

        if (usuarioDAO.existePorEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email");
        }

        // Asegurar rol CLIENTE
        rolDAO.buscarPorNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE en BD"));

        Usuario nuevo = new Usuario(
                null,
                request.nombre().trim(),
                request.apellido().trim(),
                request.fechaNacimiento(),
                email,
                request.telefono(),
                passwordEncoder.encode(request.password()),
                false,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE")
        );

        Usuario guardado = usuarioDAO.guardar(nuevo);

        TokenUsuario token = crearTokenVerificacion(guardado.id());
        TokenUsuario guardadoToken = tokenUsuarioDAO.guardar(token);

        String link = buildVerificationLink(guardadoToken.token());
        emailService.sendVerificationEmail(guardado.email(), guardado.nombre(), link);

        return new MessageResponseDTO("Registro exitoso. Revisa tu correo para verificar la cuenta.");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = normalizeEmail(request.email());

        Usuario usuario = usuarioDAO.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (usuario.estado() != EstadoUsuario.ACTIVO) {
            throw new IllegalArgumentException("Usuario no habilitado para iniciar sesión");
        }

        if (!usuario.emailVerificado()) {
            throw new IllegalArgumentException("Debes verificar tu correo antes de iniciar sesión");
        }

        if (!passwordEncoder.matches(request.password(), usuario.passwordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Generar JWT (tú defines internamente qué claims usar)
        String token = jwtService.generateToken(usuario.id(), usuario.email(), usuario.roles());

        UserSummaryDTO userSummary = new UserSummaryDTO(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.email(),
                usuario.emailVerificado(),
                usuario.estado().name(),
                usuario.roles()
        );

        return new AuthResponseDTO(token, "Bearer", jwtExpiresInSeconds, userSummary);
    }

    @Override
    @Transactional
    public MessageResponseDTO verifyEmail(VerifyEmailRequestDTO request) {
        String tokenStr = request.token().trim();
        LocalDateTime now = LocalDateTime.now();

        TokenUsuario token = tokenUsuarioDAO.buscarPorToken(tokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (token.usadoEn() != null) {
            throw new IllegalArgumentException("Este token ya fue utilizado");
        }

        if (token.expiraEn().isBefore(now)) {
            throw new IllegalArgumentException("El token ha expirado");
        }

        if (token.tipo() != TipoToken.VERIFICAR_EMAIL) {
            throw new IllegalArgumentException("Tipo de token no válido para verificación");
        }

        Usuario usuario = usuarioDAO.buscarPorId(token.usuarioId())
                .orElseThrow(() -> new IllegalStateException("Usuario asociado al token no existe"));

        if (usuario.emailVerificado()) {
            return new MessageResponseDTO("Tu correo ya estaba verificado.");
        }

        // Marcar usuario verificado
        Usuario actualizado = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.fechaNacimiento(),
                usuario.email(),
                usuario.telefono(),
                usuario.passwordHash(),
                true,
                usuario.estado(),
                usuario.ultimoLoginEn(),
                usuario.roles()
        );
        usuarioDAO.guardar(actualizado);

        // Marcar token como usado
        TokenUsuario tokenUsado = new TokenUsuario(
                token.id(),
                token.usuarioId(),
                token.token(),
                token.tipo(),
                token.creadoEn(),
                token.expiraEn(),
                now
        );
        tokenUsuarioDAO.guardar(tokenUsado);

        return new MessageResponseDTO("Correo verificado correctamente. Ya puedes iniciar sesión.");
    }

    @Override
    @Transactional
    public MessageResponseDTO resendVerification(ResendVerificationDTO request) {
        String email = normalizeEmail(request.email());

        Usuario usuario = usuarioDAO.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ese email"));

        if (usuario.emailVerificado()) {
            return new MessageResponseDTO("Tu correo ya está verificado. Inicia sesión.");
        }

        // (Opcional) invalidar tokens anteriores (marcarlos como usados)
        LocalDateTime now = LocalDateTime.now();
        tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(usuario.id(), TipoToken.VERIFICAR_EMAIL)
                .forEach(t -> tokenUsuarioDAO.guardar(new TokenUsuario(
                        t.id(), t.usuarioId(), t.token(), t.tipo(), t.creadoEn(), t.expiraEn(), now
                )));

        TokenUsuario nuevoToken = crearTokenVerificacion(usuario.id());
        TokenUsuario guardado = tokenUsuarioDAO.guardar(nuevoToken);

        String link = buildVerificationLink(guardado.token());
        emailService.sendVerificationEmail(usuario.email(), usuario.nombre(), link);

        return new MessageResponseDTO("Te reenviamos el correo de verificación.");
    }

    // ----------------- helpers -----------------

    private TokenUsuario crearTokenVerificacion(Long usuarioId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expira = now.plus(Duration.ofMinutes(verificationTokenMinutes));

        String token = UUID.randomUUID().toString().replace("-", ""); // 32 chars

        return new TokenUsuario(
                null,
                usuarioId,
                token,
                TipoToken.VERIFICAR_EMAIL,
                null,    // lo setea @CreationTimestamp en la entity
                expira,
                null
        );
    }

    private String buildVerificationLink(String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        // endpoint que tú configuras (puede ser del backend o del frontend)
        return verificationEndpoint + "?token=" + encoded;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}