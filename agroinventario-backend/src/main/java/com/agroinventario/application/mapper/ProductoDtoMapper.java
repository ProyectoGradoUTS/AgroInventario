package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.ProductoResponse;
import com.agroinventario.domain.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoDtoMapper {

    @Mapping(target = "stockBajo", expression = "java(producto.stockBajo())")
    ProductoResponse toResponse(Producto producto);

    List<ProductoResponse> toResponseList(List<Producto> productos);
}
