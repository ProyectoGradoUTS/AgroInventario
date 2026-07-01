package com.agroinventario.application.usecase.auth;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.InvalidCredentialsException;
import com.agroinventario.domain.model.AuthResult;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Usuario;
import com.agroinventario.domain.ports.input.auth.LoginUseCase;
import com.agroinventario.domain.ports.output.PasswordEncoderPort;
import com.agroinventario.domain.ports.output.TokenProviderPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LoginUseCaseImpl implements LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResult ejecutar(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, usuario.password())) {
            throw new InvalidCredentialsException();
        }

        if (usuario.estado() != EstadoGeneral.ACTIVO) {
            throw new BusinessRuleException("El usuario está inactivo");
        }

        return buildAuthResult(usuario);
    }

    private AuthResult buildAuthResult(Usuario usuario) {
        String token = tokenProvider.generateToken(usuario);
        var roles = usuario.roles().stream().map(r -> r.nombre()).collect(Collectors.toSet());

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
