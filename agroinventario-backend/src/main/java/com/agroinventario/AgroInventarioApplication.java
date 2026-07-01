package com.agroinventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 * <p>
 * {@code @SpringBootApplication} habilita autoconfiguración y escaneo de componentes
 * en {@code com.agroinventario} y todos sus subpaquetes (domain, application, infrastructure).
 * </p>
 */
@SpringBootApplication
public class AgroInventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroInventarioApplication.class, args);
    }
}
