package com.agroinventario.domain.ports.input.auth;

import com.agroinventario.domain.model.AuthResult;

public interface RegisterUsuarioUseCase {

    AuthResult ejecutar(String nombre, String email, String password);
}
