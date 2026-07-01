package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<UsuarioEntity> findWithRolesById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<UsuarioEntity> findWithRolesByEmail(String email);

    boolean existsByEmail(String email);
}
