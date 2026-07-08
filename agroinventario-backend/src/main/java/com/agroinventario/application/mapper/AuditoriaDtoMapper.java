package com.agroinventario.application.mapper;

import com.agroinventario.application.dto.response.AuditoriaResponse;
import com.agroinventario.domain.model.Auditoria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditoriaDtoMapper {

    AuditoriaResponse toResponse(Auditoria auditoria);

    List<AuditoriaResponse> toResponseList(List<Auditoria> registros);
}
