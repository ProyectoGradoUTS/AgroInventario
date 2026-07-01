package com.agroinventario.domain.model;

import java.util.Set;

/**
 * Resultado de autenticación (dominio) — token + datos del usuario.
 */
public record AuthResult(
        String token,
        String tokenType,
        long expiresInMs,
        Long usuarioId,
        String email,
        String nombre,
        Set<String> roles
) {
}
