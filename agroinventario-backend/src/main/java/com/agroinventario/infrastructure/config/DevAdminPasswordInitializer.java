package com.agroinventario.infrastructure.config;

import com.agroinventario.infrastructure.adapters.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Convierte la contraseña temporal del admin de desarrollo a BCrypt.
 */
@Component
@Profile("dev")
public class DevAdminPasswordInitializer {

    private static final String ADMIN_EMAIL = "admin@agroinventario.local";
    private static final String ADMIN_PASSWORD = "Admin123!";

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public DevAdminPasswordInitializer(
            UsuarioJpaRepository usuarioJpaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateAdminPassword() {
        usuarioJpaRepository.findWithRolesByEmail(ADMIN_EMAIL).ifPresent(usuario -> {
            if (needsMigration(usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                usuarioJpaRepository.save(usuario);
            }
        });
    }

    private boolean needsMigration(String password) {
        return password == null
                || password.startsWith("{noop}")
                || !password.startsWith("$2a$");
    }
}
