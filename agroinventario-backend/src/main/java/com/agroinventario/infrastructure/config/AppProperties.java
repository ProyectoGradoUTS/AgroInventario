package com.agroinventario.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades tipadas de la aplicación (prefijo {@code app.*} en application.yml).
 * Centraliza configuración fuera de strings mágicos en el código.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        CorsProperties cors
) {
    public record CorsProperties(String allowedOrigins) {}
}
