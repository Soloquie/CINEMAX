package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.RolDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.TokenUsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.UsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.Seguridad.JwtService;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import com.uniquindio.CINEMAX.negocio.Service.AuthService;
import com.uniquindio.CINEMAX.negocio.Service.EmailService;
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

    // --- Config ---
    @Value("${app.verification.endpoint:http://localhost:8080/api/auth/verify-email}")
    private String verificationEndpoint;

    @Value("${app.verification.token-minutes:1440}")
    private long verificationTokenMinutes;

    @Value("${app.reset-password.endpoint:http://localhost:8080/api/auth/reset-password}")
    private String resetPasswordEndpoint;

    @Value("${app.reset-password.token-minutes:30}")
    private long resetPasswordTokenMinutes;

    @Value("${app.jwt.expires-in-seconds:3600}")
    private long jwtExpiresInSeconds;

    @Value("${app.auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.auth.lock-minutes:15}")
    private int lockMinutes;

    // bcrypt("dummy") para timing-safe cuando el usuario NO existe
    private static final String DUMMY_HASH =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5pWk0pZ5s6m0qOQ8bQ4b9v9l6y9yW";

    // ----------------- AUTH -----------------

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

        // OJO: aquí se asume que tu Usuario(dom) tiene intentosFallidos y bloqueadoHasta
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
                Set.of("CLIENTE"),
                0,
                null
        );

        Usuario guardado = usuarioDAO.guardar(nuevo);

        TokenUsuario token = crearTokenVerificacion(guardado.id());
        TokenUsuario guardadoToken = tokenUsuarioDAO.guardar(token);

        String link = buildVerificationLink(guardadoToken.token());
        emailService.sendVerificationEmail(guardado.email(), guardado.nombre(), link);

        return new MessageResponseDTO("Registro exitoso. Revisa tu correo para verificar la cuenta.");
    }

    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = normalizeEmail(request.email());
        LocalDateTime now = LocalDateTime.now();

        Usuario usuario = usuarioDAO.buscarPorEmail(email).orElse(null);

        // Timing-safe + respuesta genérica
        if (usuario == null) {
            passwordEncoder.matches(request.password(), DUMMY_HASH);
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Bloqueo duro
        if (usuario.estado() == EstadoUsuario.BLOQUEADO || usuario.estado() == EstadoUsuario.INACTIVO) {
            throw new IllegalArgumentException("Usuario no habilitado para iniciar sesión");
        }

        // Bloqueo temporal
        if (usuario.bloqueadoHasta() != null && usuario.bloqueadoHasta().isAfter(now)) {
            throw new IllegalArgumentException("Cuenta temporalmente bloqueada. Intenta más tarde.");
        }

        // Email verificado
        if (!usuario.emailVerificado()) {
            throw new IllegalArgumentException("Debes verificar tu correo antes de iniciar sesión");
        }

        boolean ok = passwordEncoder.matches(request.password(), usuario.passwordHash());

        if (!ok) {
            int intentos = (usuario.intentosFallidos() == null ? 0 : usuario.intentosFallidos()) + 1;

            LocalDateTime bloqueadoHasta = usuario.bloqueadoHasta();
            if (intentos >= maxFailedAttempts) {
                bloqueadoHasta = now.plusMinutes(lockMinutes);
                intentos = 0; // opcional
            }

            Usuario actualizado = new Usuario(
                    usuario.id(),
                    usuario.nombre(),
                    usuario.apellido(),
                    usuario.fechaNacimiento(),
                    usuario.email(),
                    usuario.telefono(),
                    usuario.passwordHash(),
                    usuario.emailVerificado(),
                    usuario.estado(),
                    usuario.ultimoLoginEn(),
                    usuario.roles(),
                    intentos,
                    bloqueadoHasta
            );
            usuarioDAO.guardar(actualizado);

            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Login OK → reset intentos + limpiar bloqueo + set ultimoLogin
        Usuario actualizadoOk = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.fechaNacimiento(),
                usuario.email(),
                usuario.telefono(),
                usuario.passwordHash(),
                usuario.emailVerificado(),
                usuario.estado(),
                now,
                usuario.roles(),
                0,
                null
        );
        usuarioDAO.guardar(actualizadoOk);

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
                usuario.roles(),
                usuario.intentosFallidos(),
                usuario.bloqueadoHasta()
        );
        usuarioDAO.guardar(actualizado);

        // marcar token como usado
        tokenUsuarioDAO.guardar(new TokenUsuario(
                token.id(), token.usuarioId(), token.token(), token.tipo(), token.creadoEn(), token.expiraEn(), now
        ));

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

    // ----------------- PASSWORD RESET -----------------

    @Override
    @Transactional
    public MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO request) {
        String email = normalizeEmail(request.email());

        // Respuesta genérica (no revelar existencia)
        Usuario usuario = usuarioDAO.buscarPorEmail(email).orElse(null);
        if (usuario == null) {
            return new MessageResponseDTO("Si el correo existe, enviaremos un enlace de recuperación.");
        }

        LocalDateTime now = LocalDateTime.now();

        // invalidar tokens previos activos RESET_PASSWORD
        tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(usuario.id(), TipoToken.RESET_PASSWORD)
                .forEach(t -> tokenUsuarioDAO.guardar(new TokenUsuario(
                        t.id(), t.usuarioId(), t.token(), t.tipo(), t.creadoEn(), t.expiraEn(), now
                )));

        LocalDateTime expira = now.plusMinutes(resetPasswordTokenMinutes);
        String token = UUID.randomUUID().toString().replace("-", "");

        TokenUsuario nuevo = new TokenUsuario(
                null,
                usuario.id(),
                token,
                TipoToken.RESET_PASSWORD,
                null,
                expira,
                null
        );

        TokenUsuario guardado = tokenUsuarioDAO.guardar(nuevo);
        String link = resetPasswordEndpoint + "?token=" + URLEncoder.encode(guardado.token(), StandardCharsets.UTF_8);

        emailService.sendPasswordResetEmail(usuario.email(), usuario.nombre(), link);

        return new MessageResponseDTO("Si el correo existe, enviaremos un enlace de recuperación.");
    }

    @Override
    @Transactional
    public MessageResponseDTO resetPassword(ResetPasswordRequestDTO request) {
        String tokenStr = request.token().trim();
        LocalDateTime now = LocalDateTime.now();

        TokenUsuario token = tokenUsuarioDAO.buscarPorToken(tokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (token.usadoEn() != null) throw new IllegalArgumentException("Token ya utilizado");
        if (token.expiraEn().isBefore(now)) throw new IllegalArgumentException("Token expirado");
        if (token.tipo() != TipoToken.RESET_PASSWORD) throw new IllegalArgumentException("Token no válido para reset");

        Usuario usuario = usuarioDAO.buscarPorId(token.usuarioId())
                .orElseThrow(() -> new IllegalStateException("Usuario asociado al token no existe"));

        Usuario actualizado = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.fechaNacimiento(),
                usuario.email(),
                usuario.telefono(),
                passwordEncoder.encode(request.newPassword()),
                usuario.emailVerificado(),
                usuario.estado(),
                usuario.ultimoLoginEn(),
                usuario.roles(),
                usuario.intentosFallidos(),
                usuario.bloqueadoHasta()
        );
        usuarioDAO.guardar(actualizado);

        // marcar token como usado
        tokenUsuarioDAO.guardar(new TokenUsuario(
                token.id(), token.usuarioId(), token.token(), token.tipo(), token.creadoEn(), token.expiraEn(), now
        ));

        return new MessageResponseDTO("Contraseña actualizada correctamente.");
    }

    // ----------------- helpers -----------------

    private TokenUsuario crearTokenVerificacion(Long usuarioId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expira = now.plus(Duration.ofMinutes(verificationTokenMinutes));
        String token = UUID.randomUUID().toString().replace("-", "");

        return new TokenUsuario(
                null,
                usuarioId,
                token,
                TipoToken.VERIFICAR_EMAIL,
                null,
                expira,
                null
        );
    }

    private String buildVerificationLink(String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return verificationEndpoint + "?token=" + encoded;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}