package com.agroinventario.application.usecase.auth;

import com.agroinventario.domain.exception.EmailAlreadyExistsException;
import com.agroinventario.domain.model.AuthResult;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Rol;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.auth.RegisterUsuarioUseCase;
import com.agroinventario.domain.ports.output.PasswordEncoderPort;
import com.agroinventario.domain.ports.output.RolRepositoryPort;
import com.agroinventario.domain.ports.output.TokenProviderPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegisterUsuarioUseCaseImpl implements RegisterUsuarioUseCase {

    private static final String ROL_EMPLEADO = "EMPLEADO";

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public RegisterUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            RolRepositoryPort rolRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResult ejecutar(String nombre, String email, String password) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        Rol rolEmpleado = rolRepository.findByNombre(ROL_EMPLEADO)
                .orElseThrow(() -> new IllegalStateException("Rol EMPLEADO no configurado en la base de datos"));

        String encodedPassword = passwordEncoder.encode(password);

        Usuario nuevo = new Usuario(
                null,
                nombre,
                email,
                encodedPassword,
                EstadoGeneral.ACTIVO,
                LocalDateTime.now(),
                Set.of(rolEmpleado)
        );

        Usuario guardado = usuarioRepository.save(nuevo);
        return buildAuthResult(guardado);
    }

    private AuthResult buildAuthResult(Usuario usuario) {
        String token = tokenProvider.generateToken(usuario);
        var roles = usuario.roles().stream().map(Rol::nombre).collect(Collectors.toSet());

        return new AuthResult(
                token,
                "Bearer",
                tokenProvider.getExpirationMs(),
                usuario.id(),
                usuario.email(),
                usuario.nombre(),
                roles
        );
    }
}
