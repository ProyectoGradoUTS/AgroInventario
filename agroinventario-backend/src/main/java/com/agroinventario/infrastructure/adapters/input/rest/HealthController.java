package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.infrastructure.config.AppProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint de verificación de la Fase 1.
 * Confirma que la API REST y la configuración base están operativas.
 */
@RestController
@RequestMapping("/v1/health")
public class HealthController {

    private final AppProperties appProperties;

    public HealthController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "application", appProperties.name(),
                "version", appProperties.version(),
                "timestamp", Instant.now().toString(),
                "architecture", "hexagonal"
        ));
    }
}
