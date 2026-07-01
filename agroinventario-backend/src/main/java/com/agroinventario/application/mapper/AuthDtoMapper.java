package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.AuthResponse;
import com.agroinventario.application.dto.response.UsuarioResponse;
import com.agroinventario.domain.model.AuthResult;
import com.agroinventario.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthDtoMapper {

    AuthResponse toResponse(AuthResult authResult);

    @Mapping(target = "roles", expression = "java(usuario.roles().stream().map(r -> r.nombre()).collect(java.util.stream.Collectors.toSet()))")
    UsuarioResponse toUsuarioResponse(Usuario usuario);
}
