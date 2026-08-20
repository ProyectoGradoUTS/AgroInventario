package com.agroinventario.infrastructure.security;

import com.agroinventario.domain.ports.output.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(TokenProviderPort tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println("========== JWT FILTER ==========");
        System.out.println("URI: " + request.getRequestURI());

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("HEADER: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("NO HAY TOKEN");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        System.out.println("TOKEN VALIDO: " + tokenProvider.isTokenValid(token));

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && tokenProvider.isTokenValid(token)) {

            String email = tokenProvider.extractEmail(token);
            System.out.println("EMAIL: " + email);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            System.out.println("AUTHORITIES: " + userDetails.getAuthorities());

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("AUTENTICADO");
        }

        filterChain.doFilter(request, response);
    }
}
