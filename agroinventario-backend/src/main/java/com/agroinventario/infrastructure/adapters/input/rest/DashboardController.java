package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.DashboardEjecutivoResponse;
import com.agroinventario.application.dto.response.DashboardResumenResponse;
import com.agroinventario.application.dto.response.PrediccionProductoResponse;
import com.agroinventario.application.dto.response.RecomendacionProductoResponse;
import com.agroinventario.domain.ports.input.dashboard.DashboardEjecutivoUseCase;
import com.agroinventario.domain.ports.input.dashboard.DashboardResumenUseCase;
import com.agroinventario.domain.ports.input.dashboard.PrediccionInventarioUseCase;
import com.agroinventario.domain.ports.input.dashboard.RecomendacionReposicionUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/dashboard")
@Tag(name = "Dashboard", description = "Resumen administrativo y comportamiento por producto")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class DashboardController {

    private final DashboardResumenUseCase dashboardResumenUseCase;
    private final RecomendacionReposicionUseCase recomendacionReposicionUseCase;
    private final PrediccionInventarioUseCase prediccionInventarioUseCase;
    private final DashboardEjecutivoUseCase dashboardEjecutivoUseCase;

    public DashboardController(
            DashboardResumenUseCase dashboardResumenUseCase,
            RecomendacionReposicionUseCase recomendacionReposicionUseCase,
            PrediccionInventarioUseCase prediccionInventarioUseCase,
            DashboardEjecutivoUseCase dashboardEjecutivoUseCase) {
        this.dashboardResumenUseCase = dashboardResumenUseCase;
        this.recomendacionReposicionUseCase = recomendacionReposicionUseCase;
        this.prediccionInventarioUseCase = prediccionInventarioUseCase;
        this.dashboardEjecutivoUseCase = dashboardEjecutivoUseCase;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<DashboardResumenResponse>> resumen() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardResumenUseCase.ejecutar()));
    }

    @GetMapping("/ejecutivo")
    public ResponseEntity<ApiResponse<DashboardEjecutivoResponse>> ejecutivo() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardEjecutivoUseCase.ejecutar()));
    }

    @GetMapping("/recomendaciones")
    public ResponseEntity<ApiResponse<List<RecomendacionProductoResponse>>> recomendaciones() {
        return ResponseEntity.ok(ApiResponse.ok(recomendacionReposicionUseCase.ejecutar()));
    }

    @GetMapping("/predicciones")
    public ResponseEntity<ApiResponse<List<PrediccionProductoResponse>>> predicciones() {
        return ResponseEntity.ok(ApiResponse.ok(prediccionInventarioUseCase.ejecutar()));
    }
}
