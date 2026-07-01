package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.CategoriaResponse;
import com.agroinventario.domain.model.Categoria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaDtoMapper {

    CategoriaResponse toResponse(Categoria categoria);

    List<CategoriaResponse> toResponseList(List<Categoria> categorias);
}
