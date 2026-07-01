package com.agroinventario.application.dto.response;

import com.agroinventario.domain.model.EstadoGeneral;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        int stockActual,
        int stockMinimo,
        boolean stockBajo,
        LocalDate fechaVencimiento,
        Long categoriaId,
        String categoriaNombre,
        EstadoGeneral estado,
        LocalDateTime fechaCreacion
) {
}
