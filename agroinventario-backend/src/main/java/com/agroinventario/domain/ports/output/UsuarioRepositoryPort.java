package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
