package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.RegistrarMovimientoRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.MovimientoInventarioResponse;
import com.agroinventario.application.mapper.MovimientoDtoMapper;
import com.agroinventario.domain.ports.input.inventario.ListarMovimientosInventarioUseCase;
import com.agroinventario.domain.ports.input.inventario.RegistrarMovimientoInventarioUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/inventario")
@Validated
public class InventarioController {

    private final RegistrarMovimientoInventarioUseCase registrarMovimientoUseCase;
    private final ListarMovimientosInventarioUseCase listarMovimientosUseCase;
    private final MovimientoDtoMapper mapper;

    public InventarioController(
            RegistrarMovimientoInventarioUseCase registrarMovimientoUseCase,
            ListarMovimientosInventarioUseCase listarMovimientosUseCase,
            MovimientoDtoMapper mapper) {
        this.registrarMovimientoUseCase = registrarMovimientoUseCase;
        this.listarMovimientosUseCase = listarMovimientosUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/productos/{productoId}/movimientos")
    public ResponseEntity<ApiResponse<MovimientoInventarioResponse>> registrarMovimiento(
            @PathVariable @Positive(message = "El id del producto debe ser positivo") Long productoId,
            @Valid @RequestBody RegistrarMovimientoRequest request) {
        var movimiento = registrarMovimientoUseCase.ejecutar(
                productoId,
                request.tipoMovimiento(),
                request.cantidad(),
                request.descripcion()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movimiento registrado", mapper.toResponse(movimiento)));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<ApiResponse<List<MovimientoInventarioResponse>>> listarMovimientos(
            @RequestParam(required = false) @Positive(message = "El id del producto debe ser positivo") Long productoId) {
        return ResponseEntity.ok(ApiResponse.ok(
                mapper.toResponseList(listarMovimientosUseCase.ejecutar(productoId))));
    }
}
