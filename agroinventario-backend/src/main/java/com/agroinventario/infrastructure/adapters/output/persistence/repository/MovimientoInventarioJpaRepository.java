package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.infrastructure.adapters.output.persistence.entity.MovimientoInventarioEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioJpaRepository extends JpaRepository<MovimientoInventarioEntity, Long> {

    @EntityGraph(attributePaths = {"producto", "usuario"})
    List<MovimientoInventarioEntity> findAllByOrderByFechaMovimientoDesc();

    @EntityGraph(attributePaths = {"producto", "usuario"})
    List<MovimientoInventarioEntity> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);

    boolean existsByProductoId(Long productoId);
}
