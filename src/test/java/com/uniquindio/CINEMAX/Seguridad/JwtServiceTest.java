package com.uniquindio.CINEMAX.Seguridad;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final long ACCESS_EXPIRES = 3600;
    private static final long REFRESH_EXPIRES = 604800;

    private JwtService buildService() {
        return new JwtService(SECRET, ACCESS_EXPIRES, REFRESH_EXPIRES);
    }

    @Test
    void generarYValidarAccessToken() {
        JwtService jwtService = buildService();

        String token = jwtService.generateAccessToken(
                1L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertTrue(jwtService.isAccessToken(token));
        assertFalse(jwtService.isRefreshToken(token));
    }

    @Test
    void generarYValidarRefreshToken() {
        JwtService jwtService = buildService();

        String token = jwtService.generateRefreshToken(
                1L,
                "usuario@test.com"
        );

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertTrue(jwtService.isRefreshToken(token));
        assertFalse(jwtService.isAccessToken(token));
    }

    @Test
    void extraerEmailDesdeAccessToken() {
        JwtService jwtService = buildService();

        String token = jwtService.generateAccessToken(
                1L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        String email = jwtService.extractEmail(token);

        assertEquals("usuario@test.com", email);
    }

    @Test
    void extraerRolesDesdeAccessToken() {
        JwtService jwtService = buildService();

        String token = jwtService.generateAccessToken(
                1L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        Set<String> roles = jwtService.extractRoles(token);

        assertTrue(roles.contains("CLIENTE"));
    }

    @Test
    void generateAccessTokenContieneClaimsCorrectos() {
        JwtService jwtService = buildService();

        String token = jwtService.generateAccessToken(
                5L,
                "correo@test.com",
                Set.of("ADMIN")
        );

        assertEquals("correo@test.com", jwtService.extractEmail(token));
        assertEquals(5L, jwtService.extractUserId(token));
        assertTrue(jwtService.extractRoles(token).contains("ADMIN"));
        assertEquals("access", jwtService.extractTokenType(token));
        assertNotNull(jwtService.extractJti(token));
        assertFalse(jwtService.extractJti(token).isBlank());
    }

    @Test
    void generateRefreshTokenContieneClaimsCorrectos() {
        JwtService jwtService = buildService();

        String token = jwtService.generateRefreshToken(
                7L,
                "refresh@test.com"
        );

        assertEquals("refresh@test.com", jwtService.extractEmail(token));
        assertEquals(7L, jwtService.extractUserId(token));
        assertEquals("refresh", jwtService.extractTokenType(token));
        assertNotNull(jwtService.extractJti(token));
        assertFalse(jwtService.extractJti(token).isBlank());
        assertTrue(jwtService.extractRoles(token).isEmpty());
    }

    @Test
    void isTokenValidRetornaFalseSiTokenInvalido() {
        JwtService jwtService = buildService();

        String tokenInvalido = "token.falso.invalido";

        assertFalse(jwtService.isTokenValid(tokenInvalido));
    }

    @Test
    void extractUserIdFuncionaCorrectamente() {
        JwtService jwtService = buildService();

        String token = jwtService.generateAccessToken(
                99L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        Long userId = jwtService.extractUserId(token);

        assertEquals(99L, userId);
    }

    @Test
    void extractUserIdCuandoEsInteger() {
        JwtService jwtService = buildService();

        String token = Jwts.builder()
                .claim("uid", 10) // Integer
                .claim("typ", "access")
                .setSubject("test@test.com")
                .signWith(
                        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();

        Long userId = jwtService.extractUserId(token);

        assertEquals(10L, userId);
    }

    @Test
    void extractUserIdCuandoEsString() {
        JwtService jwtService = buildService();

        String token = Jwts.builder()
                .claim("uid", "15") // String
                .claim("typ", "access")
                .setSubject("test@test.com")
                .signWith(
                        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();

        Long userId = jwtService.extractUserId(token);

        assertEquals(15L, userId);
    }

    @Test
    void extractUserIdTipoInvalido() {
        JwtService jwtService = buildService();

        String token = Jwts.builder()
                .claim("uid", true) // tipo inesperado
                .claim("typ", "access")
                .setSubject("test@test.com")
                .signWith(
                        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();

        assertThrows(IllegalStateException.class, () -> jwtService.extractUserId(token));
    }

    @Test
    void getAccessExpiresInSecondsDevuelveValorConfigurado() {
        JwtService jwtService = buildService();

        assertEquals(ACCESS_EXPIRES, jwtService.getAccessExpiresInSeconds());
    }

    @Test
    void getRefreshExpiresInSecondsDevuelveValorConfigurado() {
        JwtService jwtService = buildService();

        assertEquals(REFRESH_EXPIRES, jwtService.getRefreshExpiresInSeconds());
    }
}