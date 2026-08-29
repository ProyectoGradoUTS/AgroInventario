package com.agroinventario.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado del asistente IA")
public record AsistenteEstadoResponse(
        @Schema(example = "true") boolean disponible,
        @Schema(example = "Asistente disponible para consultas de inventario y reposición")
        String mensaje
) {
}
