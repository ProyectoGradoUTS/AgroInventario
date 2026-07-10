package com.agroinventario.application.dto.request;

import com.agroinventario.domain.model.EstadoGeneral;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoUsuarioRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoGeneral estado
) {
}
