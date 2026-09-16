package com.agroinventario.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Respuesta del asistente de inventario")
public record AsistenteConsultaResponse(
        @Schema(example = "Hay 3 productos con stock bajo y 2 alertas pendientes.")
        String respuesta,
        @Schema(description = "Motor usado: LLM o REGLAS")
        String motor,
        @Schema(description = "Intención detectada")
        String intencion,
        @Schema(description = "Metadatos de contexto para la UI")
        Map<String, Object> metadatos
) {
}
