package com.agroinventario.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ia")
public record IaProperties(
        String provider,
        String apiKey,
        String model,
        int timeoutMs
) {
    public boolean llmHabilitado() {
        return apiKey != null
                && !apiKey.isBlank()
                && provider != null
                && !"none".equalsIgnoreCase(provider.trim());
    }
}
