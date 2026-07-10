package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.UsuarioEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioEntityMapper mapper;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository jpaRepository, UsuarioEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Usuario save(Usuario usuario) {
        var saved = jpaRepository.save(mapper.toEntity(usuario));
        return jpaRepository.findWithRolesById(saved.getId())
                .map(mapper::toDomain)
                .orElseThrow();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findWithRolesById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findWithRolesByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Usuario> findAllOrderByNombreAsc() {
        return jpaRepository.findAllByOrderByNombreAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
