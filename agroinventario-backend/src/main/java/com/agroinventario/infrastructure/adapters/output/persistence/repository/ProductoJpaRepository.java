package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long>, JpaSpecificationExecutor<ProductoEntity> {

    @EntityGraph(attributePaths = "categoria")
    List<ProductoEntity> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "categoria")
    List<ProductoEntity> findByEstadoOrderByNombreAsc(EstadoGeneral estado);

    @EntityGraph(attributePaths = "categoria")
    Optional<ProductoEntity> findWithCategoriaById(Long id);

    long countByCategoriaId(Long categoriaId);

    @EntityGraph(attributePaths = "categoria")
    List<ProductoEntity> findByEstadoAndFechaVencimientoNotNullAndFechaVencimientoLessThanEqualOrderByFechaVencimientoAsc(
            EstadoGeneral estado, LocalDate fechaLimite);
}
