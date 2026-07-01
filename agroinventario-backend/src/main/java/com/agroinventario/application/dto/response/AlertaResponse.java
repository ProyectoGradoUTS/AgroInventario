package com.agroinventario.application.dto.response;

import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.TipoAlerta;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        Long productoId,
        String productoNombre,
        TipoAlerta tipoAlerta,
        String mensaje,
        EstadoAlerta estado,
        LocalDateTime fechaGeneracion
) {
}
