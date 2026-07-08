package com.agroinventario.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Registro de beans de configuración global de la aplicación.
 */
@Configuration
@EnableConfigurationProperties({AppProperties.class, JwtProperties.class, AlertasProperties.class})
public class AppConfig {
}
