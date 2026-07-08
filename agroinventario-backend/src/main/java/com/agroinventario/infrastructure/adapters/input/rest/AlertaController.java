package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.ActualizarEstadoAlertaRequest;
import com.agroinventario.application.dto.response.AlertaResponse;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.mapper.AlertaDtoMapper;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.ports.input.alerta.ActualizarEstadoAlertaUseCase;
import com.agroinventario.domain.ports.input.alerta.ListarAlertasUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/alertas")
@Validated
@Tag(name = "Alertas", description = "Alertas de stock bajo y vencimiento próximo")
@SecurityRequirement(name = OpenApiConfig.bearerSchemeName())
public class AlertaController {

    private final ListarAlertasUseCase listarAlertasUseCase;
    private final ActualizarEstadoAlertaUseCase actualizarEstadoAlertaUseCase;
    private final AlertaDtoMapper mapper;

    public AlertaController(
            ListarAlertasUseCase listarAlertasUseCase,
            ActualizarEstadoAlertaUseCase actualizarEstadoAlertaUseCase,
            AlertaDtoMapper mapper) {
        this.listarAlertasUseCase = listarAlertasUseCase;
        this.actualizarEstadoAlertaUseCase = actualizarEstadoAlertaUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> listar(
            @RequestParam(required = false) EstadoAlerta estado) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseList(listarAlertasUseCase.ejecutar(estado))));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<AlertaResponse>> actualizarEstado(
            @PathVariable @Positive(message = "El id de la alerta debe ser positivo") Long id,
            @Valid @RequestBody ActualizarEstadoAlertaRequest request) {
        var alerta = actualizarEstadoAlertaUseCase.ejecutar(id, request.estado());
        return ResponseEntity.ok(ApiResponse.ok("Estado de alerta actualizado", mapper.toResponse(alerta)));
    }
}
