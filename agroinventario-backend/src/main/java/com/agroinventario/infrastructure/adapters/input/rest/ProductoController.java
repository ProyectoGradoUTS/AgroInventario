package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.ActualizarProductoRequest;
import com.agroinventario.application.dto.request.CambiarEstadoProductoRequest;
import com.agroinventario.application.dto.request.CrearProductoRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.PageResponse;
import com.agroinventario.application.dto.response.ProductoResponse;
import com.agroinventario.application.mapper.PageDtoMapper;
import com.agroinventario.application.mapper.ProductoDtoMapper;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.ports.input.producto.ActualizarProductoUseCase;
import com.agroinventario.domain.ports.input.producto.CambiarEstadoProductoUseCase;
import com.agroinventario.domain.ports.input.producto.CrearProductoUseCase;
import com.agroinventario.domain.ports.input.producto.EliminarProductoUseCase;
import com.agroinventario.domain.ports.input.producto.ListarProductosPaginadoUseCase;
import com.agroinventario.domain.ports.input.producto.ListarProductosUseCase;
import com.agroinventario.domain.ports.input.producto.ObtenerProductoUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/productos")
@Validated
public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ActualizarProductoUseCase actualizarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;
    private final ObtenerProductoUseCase obtenerProductoUseCase;
    private final ListarProductosUseCase listarProductosUseCase;
    private final ListarProductosPaginadoUseCase listarProductosPaginadoUseCase;
    private final CambiarEstadoProductoUseCase cambiarEstadoProductoUseCase;
    private final ProductoDtoMapper mapper;
    private final PageDtoMapper pageDtoMapper;

    public ProductoController(
            CrearProductoUseCase crearProductoUseCase,
            ActualizarProductoUseCase actualizarProductoUseCase,
            EliminarProductoUseCase eliminarProductoUseCase,
            ObtenerProductoUseCase obtenerProductoUseCase,
            ListarProductosUseCase listarProductosUseCase,
            ListarProductosPaginadoUseCase listarProductosPaginadoUseCase,
            CambiarEstadoProductoUseCase cambiarEstadoProductoUseCase,
            ProductoDtoMapper mapper,
            PageDtoMapper pageDtoMapper) {
        this.crearProductoUseCase = crearProductoUseCase;
        this.actualizarProductoUseCase = actualizarProductoUseCase;
        this.eliminarProductoUseCase = eliminarProductoUseCase;
        this.obtenerProductoUseCase = obtenerProductoUseCase;
        this.listarProductosUseCase = listarProductosUseCase;
        this.listarProductosPaginadoUseCase = listarProductosPaginadoUseCase;
        this.cambiarEstadoProductoUseCase = cambiarEstadoProductoUseCase;
        this.mapper = mapper;
        this.pageDtoMapper = pageDtoMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody CrearProductoRequest request) {
        var producto = crearProductoUseCase.ejecutar(
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.stockActual(),
                request.stockMinimo(),
                request.fechaVencimiento(),
                request.categoriaId(),
                request.estado()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto creado", mapper.toResponse(producto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listar(
            @RequestParam(required = false) EstadoGeneral estado) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseList(listarProductosUseCase.ejecutar(estado))));
    }

    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<PageResponse<ProductoResponse>>> listarPaginado(
            @RequestParam(required = false) EstadoGeneral estado,
            @RequestParam(required = false) @Positive Long categoriaId,
            @RequestParam(required = false) @Size(max = 100) String nombre,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        var result = listarProductosPaginadoUseCase.ejecutar(estado, categoriaId, nombre, page, size);
        var pageResponse = pageDtoMapper.toPageResponse(result, mapper::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(pageResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtener(
            @PathVariable @Positive(message = "El id debe ser positivo") Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(obtenerProductoUseCase.ejecutar(id))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ActualizarProductoRequest request) {
        var producto = actualizarProductoUseCase.ejecutar(
                id,
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.stockMinimo(),
                request.fechaVencimiento(),
                request.estado()
        );
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", mapper.toResponse(producto)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ProductoResponse>> cambiarEstado(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CambiarEstadoProductoRequest request) {
        var producto = cambiarEstadoProductoUseCase.ejecutar(id, request.estado());
        return ResponseEntity.ok(ApiResponse.ok("Estado del producto actualizado", mapper.toResponse(producto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        eliminarProductoUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}
