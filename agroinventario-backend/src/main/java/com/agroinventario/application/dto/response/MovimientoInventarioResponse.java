package com.agroinventario.application.dto.response;

import com.agroinventario.domain.model.TipoMovimiento;

import java.time.LocalDateTime;

public record MovimientoInventarioResponse(
        Long id,
        Long productoId,
        String productoNombre,
        TipoMovimiento tipoMovimiento,
        int cantidad,
        String descripcion,
        Long usuarioId,
        String usuarioNombre,
        LocalDateTime fechaMovimiento
) {
}
