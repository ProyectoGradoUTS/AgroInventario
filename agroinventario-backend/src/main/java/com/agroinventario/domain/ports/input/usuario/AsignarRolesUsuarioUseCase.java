package com.agroinventario.domain.ports.input.usuario;

import com.agroinventario.domain.model.Usuario;

import java.util.Set;

public interface AsignarRolesUsuarioUseCase {

    Usuario ejecutar(Long id, Set<String> nombresRoles);
}
