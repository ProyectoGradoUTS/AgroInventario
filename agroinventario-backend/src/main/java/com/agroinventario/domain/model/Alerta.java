package com.agroinventario.domain.model;

import java.time.LocalDateTime;

public record Alerta(
        Long id,
        Long productoId,
        String productoNombre,
        TipoAlerta tipoAlerta,
        String mensaje,
        EstadoAlerta estado,
        LocalDateTime fechaGeneracion
) {
}
