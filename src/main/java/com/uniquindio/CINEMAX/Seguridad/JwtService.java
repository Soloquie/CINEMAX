package com.uniquindio.CINEMAX.Seguridad;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiresInSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expires-in-seconds:3600}") long expiresInSeconds
    ) {
        // HS256 necesita mínimo 32 bytes (~32 chars) para estar seguro
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalArgumentException("app.jwt.secret debe tener al menos 32 caracteres");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresInSeconds = expiresInSeconds;
    }

    public String generateToken(Long userId, String email, Set<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresInSeconds);

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("roles", roles == null ? List.of() : new ArrayList<>(roles));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)         // subject = email
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object uid = parseAllClaims(token).get("uid");
        if (uid == null) return null;

        if (uid instanceof Integer i) return i.longValue();
        if (uid instanceof Long l) return l;
        if (uid instanceof String s) return Long.parseLong(s);

        throw new IllegalStateException("Claim uid tiene un tipo inesperado: " + uid.getClass());
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Object rolesObj = parseAllClaims(token).get("roles");
        if (rolesObj == null) return Set.of();

        if (rolesObj instanceof List<?> list) {
            Set<String> roles = new HashSet<>();
            for (Object o : list) roles.add(String.valueOf(o));
            return roles;
        }
        return Set.of();
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}