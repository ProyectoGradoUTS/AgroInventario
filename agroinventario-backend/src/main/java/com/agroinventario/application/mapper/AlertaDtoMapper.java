package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.AlertaResponse;
import com.agroinventario.domain.model.Alerta;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlertaDtoMapper {

    AlertaResponse toResponse(Alerta alerta);

    List<AlertaResponse> toResponseList(List<Alerta> alertas);
}
