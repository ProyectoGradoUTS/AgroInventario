package com.agroinventario.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales de acceso")
public record LoginRequest(
        @Schema(example = "admin@agroinventario.local")
        @NotBlank @Email String email,
        @Schema(example = "Admin123!")
        @NotBlank String password
) {
}
