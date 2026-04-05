package com.uniquindio.CINEMAX.Seguridad;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final String SECRET = "12345678901234567890123456789012";

    @Test
    void generarYValidarToken() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = jwtService.generateToken(
                1L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extraerEmail() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = jwtService.generateToken(
                1L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        String email = jwtService.extractEmail(token);

        assertEquals("usuario@test.com", email);
    }

    @Test
    void extraerRoles() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = jwtService.generateToken(
                1L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        Set<String> roles = jwtService.extractRoles(token);

        assertTrue(roles.contains("CLIENTE"));
    }

    // ---------------- NUEVOS TESTS ----------------

    @Test
    void generateTokenContieneClaimsCorrectos() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = jwtService.generateToken(
                5L,
                "correo@test.com",
                Set.of("ADMIN")
        );

        assertEquals("correo@test.com", jwtService.extractEmail(token));
        assertEquals(5L, jwtService.extractUserId(token));
        assertTrue(jwtService.extractRoles(token).contains("ADMIN"));
    }

    @Test
    void isTokenValidRetornaFalseSiTokenInvalido() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String tokenInvalido = "token.falso.invalido";

        assertFalse(jwtService.isTokenValid(tokenInvalido));
    }

    @Test
    void extractUserIdFuncionaCorrectamente() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = jwtService.generateToken(
                99L,
                "usuario@test.com",
                Set.of("CLIENTE")
        );

        Long userId = jwtService.extractUserId(token);

        assertEquals(99L, userId);
    }


    @Test
    void extractUserIdCuandoEsInteger() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = io.jsonwebtoken.Jwts.builder()
                .claim("uid", 10) // Integer
                .setSubject("test@test.com")
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET.getBytes()),
                        io.jsonwebtoken.SignatureAlgorithm.HS256
                )
                .compact();

        Long userId = jwtService.extractUserId(token);

        assertEquals(10L, userId);
    }


    @Test
    void extractUserIdCuandoEsString() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = io.jsonwebtoken.Jwts.builder()
                .claim("uid", "15") // String
                .setSubject("test@test.com")
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET.getBytes()),
                        io.jsonwebtoken.SignatureAlgorithm.HS256
                )
                .compact();

        Long userId = jwtService.extractUserId(token);

        assertEquals(15L, userId);
    }


    @Test
    void extractUserIdTipoInvalido() {

        JwtService jwtService = new JwtService(SECRET, 3600);

        String token = io.jsonwebtoken.Jwts.builder()
                .claim("uid", true) // tipo inesperado
                .setSubject("test@test.com")
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET.getBytes()),
                        io.jsonwebtoken.SignatureAlgorithm.HS256
                )
                .compact();

        assertThrows(IllegalStateException.class, () -> {
            jwtService.extractUserId(token);
        });
    }
}