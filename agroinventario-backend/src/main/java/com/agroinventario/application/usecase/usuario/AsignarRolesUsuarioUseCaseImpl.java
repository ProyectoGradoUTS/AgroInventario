package com.agroinventario.application.usecase.usuario;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.input.usuario.AsignarRolesUsuarioUseCase;
import com.agroinventario.domain.ports.output.CurrentUserPort;
import com.agroinventario.domain.ports.output.RolRepositoryPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class AsignarRolesUsuarioUseCaseImpl implements AsignarRolesUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;
    private final CurrentUserPort currentUserPort;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public AsignarRolesUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            RolRepositoryPort rolRepository,
            CurrentUserPort currentUserPort,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.currentUserPort = currentUserPort;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    @Override
    public Usuario ejecutar(Long id, Set<String> nombresRoles) {
        if (id.equals(currentUserPort.getUserId())) {
            throw new BusinessRuleException("No puede modificar sus propios roles");
        }

        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        Usuario actualizado = usuarioRepository.save(
                existente.conRoles(UsuarioRolResolver.resolverRoles(nombresRoles, rolRepository)));

        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.USUARIO,
                actualizado.id(),
                TipoAccionAuditoria.ACTUALIZAR,
                "Roles del usuario '%s' actualizados a %s".formatted(actualizado.email(), nombresRoles)
        );
        return actualizado;
    }
}
