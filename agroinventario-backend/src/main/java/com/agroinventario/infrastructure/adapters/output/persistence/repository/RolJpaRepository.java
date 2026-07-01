package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.infrastructure.adapters.output.persistence.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolJpaRepository extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByNombre(String nombre);
}
