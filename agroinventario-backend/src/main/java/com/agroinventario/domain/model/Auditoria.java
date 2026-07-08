package com.agroinventario.domain.model;

import java.time.LocalDateTime;

public record Auditoria(
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
