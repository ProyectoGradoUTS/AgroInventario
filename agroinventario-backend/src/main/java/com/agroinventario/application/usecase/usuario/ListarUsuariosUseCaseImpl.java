package com.agroinventario.application.usecase.usuario;

import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.usuario.ListarUsuariosUseCase;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarUsuariosUseCaseImpl implements ListarUsuariosUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ListarUsuariosUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> ejecutar() {
        return usuarioRepository.findAllOrderByNombreAsc();
    }
}
