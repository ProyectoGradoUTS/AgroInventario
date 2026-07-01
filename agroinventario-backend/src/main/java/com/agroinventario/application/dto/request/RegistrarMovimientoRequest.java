package com.agroinventario.application.dto.request;

import com.agroinventario.domain.model.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarMovimientoRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipoMovimiento,
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad,
        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        String descripcion
) {
}
