package com.agroinventario.domain.model;

import java.time.LocalDateTime;
import java.util.Set;

public record Usuario(
        Long id,
        String nombre,
        String email,
        String password,
        EstadoGeneral estado,
        LocalDateTime fechaCreacion,
        Set<Rol> roles
) {
    public boolean estaActivo() {
        return estado == EstadoGeneral.ACTIVO;
    }
}
