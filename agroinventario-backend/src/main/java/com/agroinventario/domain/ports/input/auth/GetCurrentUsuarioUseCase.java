package com.agroinventario.domain.ports.input.auth;

import com.agroinventario.domain.model.Usuario;

public interface GetCurrentUsuarioUseCase {

    Usuario ejecutar(String email);
}
