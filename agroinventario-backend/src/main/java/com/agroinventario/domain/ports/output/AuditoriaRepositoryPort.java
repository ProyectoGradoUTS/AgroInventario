package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.Auditoria;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.PageResult;

public interface AuditoriaRepositoryPort {

    Auditoria save(Auditoria auditoria);

    PageResult<Auditoria> findPaginado(EntidadAuditoria entidad, int page, int size);
}
