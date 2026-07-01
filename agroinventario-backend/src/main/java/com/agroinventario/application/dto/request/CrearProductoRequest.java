package com.agroinventario.application.dto.request;

import com.agroinventario.domain.model.EstadoGeneral;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
        String nombre,
        @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
        String descripcion,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
        BigDecimal precio,
        @Min(value = 0, message = "El stock actual no puede ser negativo")
        int stockActual,
        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        int stockMinimo,
        @FutureOrPresent(message = "La fecha de vencimiento no puede ser anterior a hoy")
        LocalDate fechaVencimiento,
        @NotNull(message = "La categoría es obligatoria")
        @Positive(message = "El id de categoría debe ser positivo")
        Long categoriaId,
        EstadoGeneral estado
) {
}
