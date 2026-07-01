package com.agroinventario.infrastructure.adapters.output.security;

import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.output.TokenProviderPort;
import com.agroinventario.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenAdapter implements TokenProviderPort {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenAdapter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(Usuario usuario) {
        var roles = usuario.roles().stream().map(r -> r.nombre()).collect(Collectors.toList());
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(usuario.email())
                .claim("userId", usuario.id())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    @Override
    public long getExpirationMs() {
        return jwtProperties.expirationMs();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
