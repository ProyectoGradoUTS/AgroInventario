package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.infrastructure.adapters.output.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {

    Optional<CategoriaEntity> findByNombre(String nombre);
}
