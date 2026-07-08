package com.agroinventario.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.alertas")
public record AlertasProperties(
        int diasAnticipacionVencimiento,
        String cronVencimiento
) {
}
