package com.agroinventario.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CrearUsuarioAdminRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotEmpty(message = "Debe indicar al menos un rol") Set<@NotBlank String> roles
) {
}
