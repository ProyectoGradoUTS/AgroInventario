package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.Rol;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.RolEntity;
import org.springframework.stereotype.Component;

@Component
public class RolEntityMapper {

    public Rol toDomain(RolEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Rol(entity.getId(), entity.getNombre());
    }
}
