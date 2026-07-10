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

    public Usuario conEstado(EstadoGeneral nuevoEstado) {
        return new Usuario(id, nombre, email, password, nuevoEstado, fechaCreacion, roles);
    }

    public Usuario conRoles(Set<Rol> nuevosRoles) {
        return new Usuario(id, nombre, email, password, estado, fechaCreacion, nuevosRoles);
    }

    public boolean estaActivo() {
        return estado == EstadoGeneral.ACTIVO;
    }
}
