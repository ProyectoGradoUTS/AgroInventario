package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.LoginRequest;
import com.agroinventario.application.dto.request.RegisterRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.AuthResponse;
import com.agroinventario.application.dto.response.UsuarioResponse;
import com.agroinventario.application.mapper.AuthDtoMapper;
import com.agroinventario.domain.ports.input.auth.GetCurrentUsuarioUseCase;
import com.agroinventario.domain.ports.input.auth.LoginUseCase;
import com.agroinventario.domain.ports.input.auth.RegisterUsuarioUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Autenticación", description = "Login, registro y sesión del usuario")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUsuarioUseCase registerUsuarioUseCase;
    private final GetCurrentUsuarioUseCase getCurrentUsuarioUseCase;
    private final AuthDtoMapper authDtoMapper;

    public AuthController(
            LoginUseCase loginUseCase,
            RegisterUsuarioUseCase registerUsuarioUseCase,
            GetCurrentUsuarioUseCase getCurrentUsuarioUseCase,
            AuthDtoMapper authDtoMapper) {
        this.loginUseCase = loginUseCase;
        this.registerUsuarioUseCase = registerUsuarioUseCase;
        this.getCurrentUsuarioUseCase = getCurrentUsuarioUseCase;
        this.authDtoMapper = authDtoMapper;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un token JWT para usar en el resto de la API")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        var result = loginUseCase.ejecutar(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso", authDtoMapper.toResponse(result)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un usuario con rol EMPLEADO y devuelve JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var result = registerUsuarioUseCase.ejecutar(
                request.nombre(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario registrado", authDtoMapper.toResponse(result)));
    }

    @GetMapping("/me")
    @Operation(summary = "Usuario autenticado", security = @SecurityRequirement(name = OpenApiConfig.bearerSchemeName()))
    public ResponseEntity<ApiResponse<UsuarioResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        var usuario = getCurrentUsuarioUseCase.ejecutar(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(authDtoMapper.toUsuarioResponse(usuario)));
    }
}
