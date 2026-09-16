package com.agroinventario.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configuración JPA activa solo cuando existe {@link DataSource} (PostgreSQL disponible).
 * Los repositorios Spring Data se habilitarán en Fase 2.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ConditionalOnBean(DataSource.class)
@EnableJpaRepositories(basePackages = "com.agroinventario.infrastructure.adapters.output.persistence")
public class JpaConfig {
}
