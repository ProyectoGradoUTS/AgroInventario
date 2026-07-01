package com.agroinventario.domain.model;

import java.time.LocalDateTime;

public record MovimientoInventario(
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
