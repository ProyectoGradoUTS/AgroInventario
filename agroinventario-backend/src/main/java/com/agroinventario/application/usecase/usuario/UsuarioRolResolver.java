package com.agroinventario.application.usecase.usuario;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Rol;
import com.agroinventario.domain.ports.output.RolRepositoryPort;

import java.util.LinkedHashSet;
import java.util.Set;

final class UsuarioRolResolver {

    private UsuarioRolResolver() {
    }

    static Set<Rol> resolverRoles(Set<String> nombresRoles, RolRepositoryPort rolRepository) {
        if (nombresRoles == null || nombresRoles.isEmpty()) {
            throw new BusinessRuleException("Debe asignar al menos un rol al usuario");
        }

        Set<Rol> roles = new LinkedHashSet<>();
        for (String nombreRol : nombresRoles) {
            Rol rol = rolRepository.findByNombre(nombreRol.toUpperCase())
                    .orElseThrow(() -> new BusinessRuleException("Rol no válido: " + nombreRol));
            roles.add(rol);
        }
        return roles;
    }
}
