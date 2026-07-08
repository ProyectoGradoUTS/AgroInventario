package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.AuditoriaResponse;
import com.agroinventario.application.dto.response.PageResponse;
import com.agroinventario.application.mapper.AuditoriaDtoMapper;
import com.agroinventario.application.mapper.PageDtoMapper;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.ports.input.auditoria.ListarAuditoriaUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auditoria")
@Validated
@Tag(name = "Auditoría", description = "Trazabilidad de operaciones (solo ADMIN)")
@SecurityRequirement(name = OpenApiConfig.bearerSchemeName())
public class AuditoriaController {

    private final ListarAuditoriaUseCase listarAuditoriaUseCase;
    private final AuditoriaDtoMapper mapper;
    private final PageDtoMapper pageDtoMapper;

    public AuditoriaController(
            ListarAuditoriaUseCase listarAuditoriaUseCase,
            AuditoriaDtoMapper mapper,
            PageDtoMapper pageDtoMapper) {
        this.listarAuditoriaUseCase = listarAuditoriaUseCase;
        this.mapper = mapper;
        this.pageDtoMapper = pageDtoMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditoriaResponse>>> listar(
            @RequestParam(required = false) EntidadAuditoria entidad,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        var resultado = listarAuditoriaUseCase.ejecutar(entidad, page, size);
        var pageResponse = pageDtoMapper.toPageResponse(resultado, mapper::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(pageResponse));
    }
}
