package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.Auditoria;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.PageResult;
import com.agroinventario.domain.ports.output.AuditoriaRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.AuditoriaEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.AuditoriaJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaPersistenceAdapter implements AuditoriaRepositoryPort {

    private final AuditoriaJpaRepository jpaRepository;
    private final AuditoriaEntityMapper mapper;

    public AuditoriaPersistenceAdapter(AuditoriaJpaRepository jpaRepository, AuditoriaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Auditoria save(Auditoria auditoria) {
        var saved = jpaRepository.save(mapper.toEntity(auditoria));
        return mapper.toDomain(saved);
    }

    @Override
    public PageResult<Auditoria> findPaginado(EntidadAuditoria entidad, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("fechaEvento").descending());
        var result = entidad == null
                ? jpaRepository.findAllByOrderByFechaEventoDesc(pageable)
                : jpaRepository.findByEntidadOrderByFechaEventoDesc(entidad, pageable);

        return new PageResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
