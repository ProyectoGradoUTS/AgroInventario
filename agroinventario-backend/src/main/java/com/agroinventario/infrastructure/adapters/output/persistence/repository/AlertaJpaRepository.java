package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.TipoAlerta;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.AlertaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertaJpaRepository extends JpaRepository<AlertaEntity, Long> {

    @EntityGraph(attributePaths = "producto")
    List<AlertaEntity> findAllByOrderByFechaGeneracionDesc();

    @EntityGraph(attributePaths = "producto")
    List<AlertaEntity> findByEstadoOrderByFechaGeneracionDesc(EstadoAlerta estado);

    Optional<AlertaEntity> findByProductoIdAndTipoAlertaAndEstado(
            Long productoId, TipoAlerta tipoAlerta, EstadoAlerta estado);
}
