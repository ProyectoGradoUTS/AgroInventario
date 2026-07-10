package com.agroinventario.support;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.RolEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.RolJpaRepository;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
@Profile("test")
public class IntegrationTestDataConfig {

    public static final String ADMIN_EMAIL = "admin@test.local";
    public static final String ADMIN_PASSWORD = "Admin123!";
    public static final String EMPLEADO_EMAIL = "empleado@test.local";
    public static final String EMPLEADO_PASSWORD = "Empleado123!";

    @Bean
    ApplicationRunner integrationTestDataRunner(
            RolJpaRepository rolRepository,
            UsuarioJpaRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return (ApplicationArguments args) -> seedUsers(rolRepository, usuarioRepository, passwordEncoder);
    }

    private void seedUsers(
            RolJpaRepository rolRepository,
            UsuarioJpaRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        RolEntity adminRol = rolRepository.findByNombre("ADMIN").orElseGet(() -> {
            RolEntity rol = new RolEntity();
            rol.setNombre("ADMIN");
            return rolRepository.save(rol);
        });
        RolEntity empleadoRol = rolRepository.findByNombre("EMPLEADO").orElseGet(() -> {
            RolEntity rol = new RolEntity();
            rol.setNombre("EMPLEADO");
            return rolRepository.save(rol);
        });

        if (usuarioRepository.findWithRolesByEmail(ADMIN_EMAIL).isEmpty()) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Admin Test");
            admin.setEmail(ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setEstado(EstadoGeneral.ACTIVO);
            admin.setFechaCreacion(LocalDateTime.now());
            admin.setRoles(Set.of(adminRol));
            usuarioRepository.save(admin);
        }

        if (usuarioRepository.findWithRolesByEmail(EMPLEADO_EMAIL).isEmpty()) {
            UsuarioEntity empleado = new UsuarioEntity();
            empleado.setNombre("Empleado Test");
            empleado.setEmail(EMPLEADO_EMAIL);
            empleado.setPassword(passwordEncoder.encode(EMPLEADO_PASSWORD));
            empleado.setEstado(EstadoGeneral.ACTIVO);
            empleado.setFechaCreacion(LocalDateTime.now());
            empleado.setRoles(Set.of(empleadoRol));
            usuarioRepository.save(empleado);
        }
    }
}
