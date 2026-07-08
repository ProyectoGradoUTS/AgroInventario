package com.agroinventario.infrastructure.adapters.output.persistence.repository;

import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.AuditoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaEntity, Long> {

    Page<AuditoriaEntity> findByEntidadOrderByFechaEventoDesc(EntidadAuditoria entidad, Pageable pageable);

    Page<AuditoriaEntity> findAllByOrderByFechaEventoDesc(Pageable pageable);
}
