package com.agroinventario.application.dto.response;

import com.agroinventario.domain.model.EstadoGeneral;

import java.time.LocalDateTime;
import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        EstadoGeneral estado,
        Set<String> roles,
        LocalDateTime fechaCreacion
) {
}
