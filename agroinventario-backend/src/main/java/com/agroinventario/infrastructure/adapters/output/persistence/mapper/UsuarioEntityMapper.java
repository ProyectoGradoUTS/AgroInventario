package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.Rol;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.RolEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.RolJpaRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class UsuarioEntityMapper {

    private final RolJpaRepository rolJpaRepository;

    public UsuarioEntityMapper(RolJpaRepository rolJpaRepository) {
        this.rolJpaRepository = rolJpaRepository;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        var roles = entity.getRoles().stream()
                .map(r -> new Rol(r.getId(), r.getNombre()))
                .collect(Collectors.toSet());

        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getEstado(),
                entity.getFechaCreacion(),
                roles
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.id());
        entity.setNombre(domain.nombre());
        entity.setEmail(domain.email());
        entity.setPassword(domain.password());
        entity.setEstado(domain.estado());
        if (domain.fechaCreacion() != null) {
            entity.setFechaCreacion(domain.fechaCreacion());
        }

        var rolesEntities = new HashSet<RolEntity>();
        for (Rol rol : domain.roles()) {
            RolEntity rolEntity = rol.id() != null
                    ? rolJpaRepository.findById(rol.id()).orElseGet(() -> resolveRolByNombre(rol))
                    : resolveRolByNombre(rol);
            rolesEntities.add(rolEntity);
        }
        entity.setRoles(rolesEntities);
        return entity;
    }

    private RolEntity resolveRolByNombre(Rol rol) {
        return rolJpaRepository.findByNombre(rol.nombre())
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + rol.nombre()));
    }
}
