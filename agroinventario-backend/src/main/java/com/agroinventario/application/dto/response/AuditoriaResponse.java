package com.agroinventario.application.dto.response;

import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;

import java.time.LocalDateTime;

public record AuditoriaResponse(
        Long id,
        EntidadAuditoria entidad,
        Long entidadId,
        TipoAccionAuditoria accion,
        String detalle,
        Long usuarioId,
        String usuarioEmail,
        LocalDateTime fechaEvento
) {
}
