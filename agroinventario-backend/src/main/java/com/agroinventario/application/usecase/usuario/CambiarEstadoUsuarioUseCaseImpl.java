package com.agroinventario.application.usecase.usuario;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.input.usuario.CambiarEstadoUsuarioUseCase;
import com.agroinventario.domain.ports.output.CurrentUserPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoUsuarioUseCaseImpl implements CambiarEstadoUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final CurrentUserPort currentUserPort;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public CambiarEstadoUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            CurrentUserPort currentUserPort,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.usuarioRepository = usuarioRepository;
        this.currentUserPort = currentUserPort;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    @Override
    public Usuario ejecutar(Long id, EstadoGeneral estado) {
        if (id.equals(currentUserPort.getUserId())) {
            throw new BusinessRuleException("No puede cambiar su propio estado");
        }

        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        Usuario actualizado = usuarioRepository.save(existente.conEstado(estado));
        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.USUARIO,
                actualizado.id(),
                TipoAccionAuditoria.CAMBIAR_ESTADO,
                "Usuario '%s' cambió a estado %s".formatted(actualizado.email(), estado)
        );
        return actualizado;
    }
}
