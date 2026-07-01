package com.agroinventario.application.usecase.auth;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.auth.GetCurrentUsuarioUseCase;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCurrentUsuarioUseCaseImpl implements GetCurrentUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public GetCurrentUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario ejecutar(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
