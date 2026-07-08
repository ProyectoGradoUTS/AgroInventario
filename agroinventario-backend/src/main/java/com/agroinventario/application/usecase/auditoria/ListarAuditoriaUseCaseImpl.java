package com.agroinventario.application.usecase.auditoria;

import com.agroinventario.domain.model.Auditoria;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.PageResult;
import com.agroinventario.domain.ports.input.auditoria.ListarAuditoriaUseCase;
import com.agroinventario.domain.ports.output.AuditoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarAuditoriaUseCaseImpl implements ListarAuditoriaUseCase {

    private final AuditoriaRepositoryPort auditoriaRepository;

    public ListarAuditoriaUseCaseImpl(AuditoriaRepositoryPort auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public PageResult<Auditoria> ejecutar(EntidadAuditoria entidad, int page, int size) {
        return auditoriaRepository.findPaginado(entidad, page, size);
    }
}
