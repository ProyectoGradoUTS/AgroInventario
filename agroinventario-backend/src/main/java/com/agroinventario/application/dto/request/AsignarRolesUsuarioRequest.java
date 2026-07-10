package com.agroinventario.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record AsignarRolesUsuarioRequest(
        @NotEmpty(message = "Debe indicar al menos un rol")
        Set<@NotBlank String> roles
) {
}
