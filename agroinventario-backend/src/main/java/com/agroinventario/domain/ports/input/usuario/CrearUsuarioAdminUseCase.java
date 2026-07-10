package com.agroinventario.domain.ports.input.usuario;

import com.agroinventario.domain.model.Usuario;

import java.util.Set;

public interface CrearUsuarioAdminUseCase {

    Usuario ejecutar(String nombre, String email, String password, Set<String> nombresRoles);
}
