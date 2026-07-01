package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.Usuario;

/**
 * Puerto de salida para generación y validación de tokens JWT.
 */
public interface TokenProviderPort {

    String generateToken(Usuario usuario);

    boolean isTokenValid(String token);

    String extractEmail(String token);

    Long extractUserId(String token);

    long getExpirationMs();
}
