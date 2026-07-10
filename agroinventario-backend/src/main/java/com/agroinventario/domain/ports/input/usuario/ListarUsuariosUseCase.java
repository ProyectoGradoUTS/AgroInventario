package com.agroinventario.domain.ports.input.usuario;

import com.agroinventario.domain.model.Usuario;

import java.util.List;

public interface ListarUsuariosUseCase {

    List<Usuario> ejecutar();
}
