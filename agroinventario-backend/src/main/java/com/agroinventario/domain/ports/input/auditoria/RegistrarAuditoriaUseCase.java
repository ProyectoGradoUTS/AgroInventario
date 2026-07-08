package com.agroinventario.domain.ports.input.auditoria;

import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;

public interface RegistrarAuditoriaUseCase {

    void ejecutar(EntidadAuditoria entidad, Long entidadId, TipoAccionAuditoria accion, String detalle);
}
