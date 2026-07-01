package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.MovimientoInventarioResponse;
import com.agroinventario.domain.model.MovimientoInventario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoDtoMapper {

    MovimientoInventarioResponse toResponse(MovimientoInventario movimiento);

    List<MovimientoInventarioResponse> toResponseList(List<MovimientoInventario> movimientos);
}
