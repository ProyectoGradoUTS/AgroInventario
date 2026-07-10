package com.agroinventario.application.usecase.usuario;

import com.agroinventario.domain.exception.EmailAlreadyExistsException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.input.usuario.CrearUsuarioAdminUseCase;
import com.agroinventario.domain.ports.output.PasswordEncoderPort;
import com.agroinventario.domain.ports.output.RolRepositoryPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Transactional
public class CrearUsuarioAdminUseCaseImpl implements CrearUsuarioAdminUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public CrearUsuarioAdminUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            RolRepositoryPort rolRepository,
            PasswordEncoderPort passwordEncoder,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    @Override
    public Usuario ejecutar(String nombre, String email, String password, Set<String> nombresRoles) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        Usuario nuevo = new Usuario(
                null,
                nombre,
                email,
                passwordEncoder.encode(password),
                EstadoGeneral.ACTIVO,
                LocalDateTime.now(),
                UsuarioRolResolver.resolverRoles(nombresRoles, rolRepository)
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.USUARIO,
                guardado.id(),
                TipoAccionAuditoria.CREAR,
                "Usuario '%s' creado por administrador con roles %s".formatted(
                        guardado.email(), nombresRoles)
        );
        return guardado;
    }
}
