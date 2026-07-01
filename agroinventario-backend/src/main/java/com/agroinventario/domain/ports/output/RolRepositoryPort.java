package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.Rol;

import java.util.Optional;

public interface RolRepositoryPort {

    Optional<Rol> findByNombre(String nombre);
}
