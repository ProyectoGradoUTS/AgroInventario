package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.Rol;
import com.agroinventario.domain.ports.output.RolRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.RolEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.RolJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RolPersistenceAdapter implements RolRepositoryPort {

    private final RolJpaRepository jpaRepository;
    private final RolEntityMapper mapper;

    public RolPersistenceAdapter(RolJpaRepository jpaRepository, RolEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Rol> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }
}
