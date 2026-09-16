package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.AsistenteConsultaRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.AsistenteConsultaResponse;
import com.agroinventario.application.dto.response.AsistenteEstadoResponse;
import com.agroinventario.domain.ports.input.asistente.ConsultarAsistenteUseCase;
import com.agroinventario.domain.ports.input.asistente.ObtenerEstadoAsistenteUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/asistente")
@Tag(name = "Asistente IA", description = "Consulta en lenguaje natural del inventario, predicción y reposición")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class AsistenteController {

    private final ObtenerEstadoAsistenteUseCase obtenerEstadoAsistenteUseCase;
    private final ConsultarAsistenteUseCase consultarAsistenteUseCase;

    public AsistenteController(
            ObtenerEstadoAsistenteUseCase obtenerEstadoAsistenteUseCase,
            ConsultarAsistenteUseCase consultarAsistenteUseCase) {
        this.obtenerEstadoAsistenteUseCase = obtenerEstadoAsistenteUseCase;
        this.consultarAsistenteUseCase = consultarAsistenteUseCase;
    }

    @GetMapping("/estado")
    @Operation(summary = "Estado del asistente")
    public ResponseEntity<ApiResponse<AsistenteEstadoResponse>> estado() {
        return ResponseEntity.ok(ApiResponse.ok(obtenerEstadoAsistenteUseCase.ejecutar()));
    }

    @PostMapping("/consultar")
    @Operation(summary = "Consulta al asistente en lenguaje natural")
    public ResponseEntity<ApiResponse<AsistenteConsultaResponse>> consultar(
            @Valid @RequestBody AsistenteConsultaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Consulta atendida", consultarAsistenteUseCase.ejecutar(request.pregunta())));
    }
}
