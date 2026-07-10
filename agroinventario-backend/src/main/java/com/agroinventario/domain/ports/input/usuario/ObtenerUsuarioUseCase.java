package com.agroinventario.domain.ports.input.usuario;

import com.agroinventario.domain.model.Usuario;

public interface ObtenerUsuarioUseCase {

    Usuario ejecutar(Long id);
}
