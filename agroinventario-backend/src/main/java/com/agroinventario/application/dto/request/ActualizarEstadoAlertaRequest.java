package com.agroinventario.application.dto.request;

import com.agroinventario.domain.model.EstadoAlerta;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoAlertaRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoAlerta estado
) {
}
