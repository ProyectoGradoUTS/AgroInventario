package com.agroinventario.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        public static final String BEARER_SCHEME = "Bearer Authentication";

        @Bean
        public OpenAPI openAPI(
                        AppProperties appProperties,
                        @Value("${server.servlet.context-path:}") String contextPath,
                        @Value("${server.port:8080}") int serverPort) {

                String basePath = contextPath.isBlank() ? "" : contextPath;

                return new OpenAPI()
                                .info(new Info()
                                                .title(appProperties.name())
                                                .description("""
                                                                API REST del proyecto de grado: asistente inteligente para la
                                                                gestión automatizada de inventarios agropecuarios.

                                                                **Autenticación JWT**
                                                                1. Ejecuta POST /v1/auth/login con email y contraseña.
                                                                2. Copia el accessToken de la respuesta.
                                                                3. Pulsa **Authorize** arriba e ingresa: Bearer <token>
                                                                """)
                                                .version(appProperties.version())
                                                .contact(new Contact().name("Agro Inventario")
                                                                .email("admin@agroinventario.local"))
                                                .license(new License().name("Proyecto de grado")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .addServersItem(new Server()
                                                .url("http://localhost:" + serverPort + basePath)
                                                .description("Local"))
                                .components(new Components()
                                                .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("Token JWT obtenido desde POST /v1/auth/login")));
        }

        public static String bearerSchemeName() {
                return BEARER_SCHEME;
        }

}