package com.agroinventario.infrastructure.adapters.input.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroinventario.application.dto.request.AsignarRolesUsuarioRequest;
import com.agroinventario.application.dto.request.CambiarEstadoUsuarioRequest;
import com.agroinventario.application.dto.request.CrearUsuarioAdminRequest;
import com.agroinventario.application.dto.response.ApiResponse;
import com.agroinventario.application.dto.response.UsuarioResponse;
import com.agroinventario.application.mapper.AuthDtoMapper;
import com.agroinventario.domain.ports.input.usuario.AsignarRolesUsuarioUseCase;
import com.agroinventario.domain.ports.input.usuario.CambiarEstadoUsuarioUseCase;
import com.agroinventario.domain.ports.input.usuario.CrearUsuarioAdminUseCase;
import com.agroinventario.domain.ports.input.usuario.ListarUsuariosUseCase;
import com.agroinventario.domain.ports.input.usuario.ObtenerUsuarioUseCase;
import com.agroinventario.infrastructure.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/usuarios")
@Validated
@Tag(name = "Usuarios", description = "Gestión de usuarios (solo ADMIN)")
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class UsuarioController {

        private final ListarUsuariosUseCase listarUsuariosUseCase;
        private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
        private final CrearUsuarioAdminUseCase crearUsuarioAdminUseCase;
        private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
        private final AsignarRolesUsuarioUseCase asignarRolesUsuarioUseCase;
        private final AuthDtoMapper authDtoMapper;

        public UsuarioController(
                        ListarUsuariosUseCase listarUsuariosUseCase,
                        ObtenerUsuarioUseCase obtenerUsuarioUseCase,
                        CrearUsuarioAdminUseCase crearUsuarioAdminUseCase,
                        CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase,
                        AsignarRolesUsuarioUseCase asignarRolesUsuarioUseCase,
                        AuthDtoMapper authDtoMapper) {
                this.listarUsuariosUseCase = listarUsuariosUseCase;
                this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
                this.crearUsuarioAdminUseCase = crearUsuarioAdminUseCase;
                this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
                this.asignarRolesUsuarioUseCase = asignarRolesUsuarioUseCase;
                this.authDtoMapper = authDtoMapper;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar() {
                var usuarios = listarUsuariosUseCase.ejecutar().stream()
                                .map(authDtoMapper::toUsuarioResponse)
                                .toList();
                return ResponseEntity.ok(ApiResponse.ok(usuarios));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<UsuarioResponse>> obtener(
                        @PathVariable @Positive(message = "El id debe ser positivo") Long id) {
                return ResponseEntity.ok(ApiResponse.ok(
                                authDtoMapper.toUsuarioResponse(obtenerUsuarioUseCase.ejecutar(id))));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<UsuarioResponse>> crear(
                        @Valid @RequestBody CrearUsuarioAdminRequest request) {
                var usuario = crearUsuarioAdminUseCase.ejecutar(
                                request.nombre(), request.email(), request.password(), request.roles());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.ok("Usuario creado", authDtoMapper.toUsuarioResponse(usuario)));
        }

        @PatchMapping("/{id}/estado")
        public ResponseEntity<ApiResponse<UsuarioResponse>> cambiarEstado(
                        @PathVariable @Positive Long id,
                        @Valid @RequestBody CambiarEstadoUsuarioRequest request) {
                var usuario = cambiarEstadoUsuarioUseCase.ejecutar(id, request.estado());
                return ResponseEntity.ok(ApiResponse.ok(
                                "Estado actualizado", authDtoMapper.toUsuarioResponse(usuario)));
        }

        @PutMapping("/{id}/roles")
        public ResponseEntity<ApiResponse<UsuarioResponse>> asignarRoles(
                        @PathVariable @Positive Long id,
                        @Valid @RequestBody AsignarRolesUsuarioRequest request) {
                var usuario = asignarRolesUsuarioUseCase.ejecutar(id, request.roles());
                return ResponseEntity.ok(ApiResponse.ok(
                                "Roles actualizados", authDtoMapper.toUsuarioResponse(usuario)));
        }
}
