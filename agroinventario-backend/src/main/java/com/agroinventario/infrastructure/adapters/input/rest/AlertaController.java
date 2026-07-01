package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.response.AlertaResponse;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.mapper.AlertaDtoMapper;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.ports.input.alerta.ListarAlertasUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/alertas")
public class AlertaController {

    private final ListarAlertasUseCase listarAlertasUseCase;
    private final AlertaDtoMapper mapper;

    public AlertaController(ListarAlertasUseCase listarAlertasUseCase, AlertaDtoMapper mapper) {
        this.listarAlertasUseCase = listarAlertasUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> listar(
            @RequestParam(required = false) EstadoAlerta estado) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseList(listarAlertasUseCase.ejecutar(estado))));
    }
}
