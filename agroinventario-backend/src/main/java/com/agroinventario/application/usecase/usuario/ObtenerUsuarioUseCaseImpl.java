package com.agroinventario.application.usecase.usuario;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.usuario.ObtenerUsuarioUseCase;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerUsuarioUseCaseImpl implements ObtenerUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ObtenerUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario ejecutar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }
}
