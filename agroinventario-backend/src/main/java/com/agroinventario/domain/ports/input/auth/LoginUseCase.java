package com.agroinventario.domain.ports.input.auth;

import com.agroinventario.domain.model.AuthResult;

public interface LoginUseCase {

    AuthResult ejecutar(String email, String password);
}
