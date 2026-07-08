package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.Auditoria;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.AuditoriaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaEntityMapper {

    public Auditoria toDomain(AuditoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Auditoria(
                entity.getId(),
                entity.getEntidad(),
                entity.getEntidadId(),
                entity.getAccion(),
                entity.getDetalle(),
                entity.getUsuarioId(),
                entity.getUsuarioEmail(),
                entity.getFechaEvento()
        );
    }

    public AuditoriaEntity toEntity(Auditoria domain) {
        AuditoriaEntity entity = new AuditoriaEntity();
        entity.setId(domain.id());
        entity.setEntidad(domain.entidad());
        entity.setEntidadId(domain.entidadId());
        entity.setAccion(domain.accion());
        entity.setDetalle(domain.detalle());
        entity.setUsuarioId(domain.usuarioId());
        entity.setUsuarioEmail(domain.usuarioEmail());
        entity.setFechaEvento(domain.fechaEvento());
        return entity;
    }
}
