package com.agroinventario.application.dto.response;

import java.util.Set;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        Long usuarioId,
        String email,
        String nombre,
        Set<String> roles
) {
}
