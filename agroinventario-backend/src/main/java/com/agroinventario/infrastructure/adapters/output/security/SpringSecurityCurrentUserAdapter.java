package com.agroinventario.infrastructure.adapters.output.security;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.ports.output.CurrentUserPort;
import com.agroinventario.domain.ports.output.TokenProviderPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SpringSecurityCurrentUserAdapter implements CurrentUserPort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final TokenProviderPort tokenProvider;

    public SpringSecurityCurrentUserAdapter(
            UsuarioRepositoryPort usuarioRepository,
            TokenProviderPort tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessRuleException("Usuario no autenticado");
        }
        return auth.getName();
    }

    @Override
    public Long getUserId() {
        Long fromToken = extractUserIdFromBearerToken();
        if (fromToken != null) {
            return fromToken;
        }
        return usuarioRepository.findByEmail(getEmail())
                .map(com.agroinventario.domain.model.Usuario::id)
                .orElseThrow(() -> new BusinessRuleException("Usuario autenticado no encontrado"));
    }

    @Override
    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleName::equals);
    }

    private Long extractUserIdFromBearerToken() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return null;
        }
        HttpServletRequest request = servletAttributes.getRequest();
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.substring(7);
        if (!tokenProvider.isTokenValid(token)) {
            return null;
        }
        return tokenProvider.extractUserId(token);
    }
}
