package com.agroinventario.infrastructure.security;

import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositoryPort usuarioRepository;

    public CustomUserDetailsService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        var authorities = usuario.roles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.nombre()))
                .toList();

        return User.builder()
                .username(usuario.email())
                .password(usuario.password())
                .authorities(authorities)
                .disabled(usuario.estado() != com.agroinventario.domain.model.EstadoGeneral.ACTIVO)
                .build();
    }
}
