package com.agroinventario.infrastructure.adapters.input.rest;

import com.agroinventario.application.dto.request.ActualizarCategoriaRequest;
import com.agroinventario.application.dto.request.CrearCategoriaRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.CategoriaResponse;
import com.agroinventario.application.mapper.CategoriaDtoMapper;
import com.agroinventario.domain.ports.input.categoria.ActualizarCategoriaUseCase;
import com.agroinventario.domain.ports.input.categoria.CrearCategoriaUseCase;
import com.agroinventario.domain.ports.input.categoria.EliminarCategoriaUseCase;
import com.agroinventario.domain.ports.input.categoria.ListarCategoriasUseCase;
import com.agroinventario.domain.ports.input.categoria.ObtenerCategoriaUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/categorias")
@Validated
@Tag(name = "Categorías", description = "Gestión del catálogo de categorías de productos")
@SecurityRequirement(name = OpenApiConfig.bearerSchemeName())
public class CategoriaController {

    private final CrearCategoriaUseCase crearCategoriaUseCase;
    private final ActualizarCategoriaUseCase actualizarCategoriaUseCase;
    private final EliminarCategoriaUseCase eliminarCategoriaUseCase;
    private final ObtenerCategoriaUseCase obtenerCategoriaUseCase;
    private final ListarCategoriasUseCase listarCategoriasUseCase;
    private final CategoriaDtoMapper mapper;

    public CategoriaController(
            CrearCategoriaUseCase crearCategoriaUseCase,
            ActualizarCategoriaUseCase actualizarCategoriaUseCase,
            EliminarCategoriaUseCase eliminarCategoriaUseCase,
            ObtenerCategoriaUseCase obtenerCategoriaUseCase,
            ListarCategoriasUseCase listarCategoriasUseCase,
            CategoriaDtoMapper mapper) {
        this.crearCategoriaUseCase = crearCategoriaUseCase;
        this.actualizarCategoriaUseCase = actualizarCategoriaUseCase;
        this.eliminarCategoriaUseCase = eliminarCategoriaUseCase;
        this.obtenerCategoriaUseCase = obtenerCategoriaUseCase;
        this.listarCategoriasUseCase = listarCategoriasUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CrearCategoriaRequest request) {
        var categoria = crearCategoriaUseCase.ejecutar(request.nombre(), request.descripcion());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Categoría creada", mapper.toResponse(categoria)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponseList(listarCategoriasUseCase.ejecutar())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtener(
            @PathVariable @Positive(message = "El id debe ser positivo") Long id) {
        return ResponseEntity.ok(ApiResponse.ok(mapper.toResponse(obtenerCategoriaUseCase.ejecutar(id))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ActualizarCategoriaRequest request) {
        var categoria = actualizarCategoriaUseCase.ejecutar(id, request.nombre(), request.descripcion());
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada", mapper.toResponse(categoria)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        eliminarCategoriaUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}
