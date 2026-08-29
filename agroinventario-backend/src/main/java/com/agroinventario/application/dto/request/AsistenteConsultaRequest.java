package com.agroinventario.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Consulta del asistente de inventario")
public record AsistenteConsultaRequest(
        @Schema(example = "¿Qué productos tienen stock bajo?")
        @NotBlank(message = "La pregunta no puede estar vacía")
        @Size(max = 1000, message = "La pregunta no puede exceder 1000 caracteres")
        String pregunta
) {
}
