package com.agroinventario.domain.ports.input.auditoria;

import com.agroinventario.domain.model.Auditoria;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.PageResult;

public interface ListarAuditoriaUseCase {

    PageResult<Auditoria> ejecutar(EntidadAuditoria entidad, int page, int size);
}
