package com.agroinventario.domain.ports.output;

/**
 * Puerto de salida para encriptación de contraseñas (BCrypt en infraestructura).
 */
public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
