package com.agroinventario.infrastructure.config;

import com.agroinventario.domain.service.AlertaDomainService;
import com.agroinventario.domain.service.InventarioDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public InventarioDomainService inventarioDomainService() {
        return new InventarioDomainService();
    }

    @Bean
    public AlertaDomainService alertaDomainService() {
        return new AlertaDomainService();
    }
}
