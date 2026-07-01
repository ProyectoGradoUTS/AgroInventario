package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.Categoria;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.CategoriaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoriaEntityMapper {

    public Categoria toDomain(CategoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Categoria(entity.getId(), entity.getNombre(), entity.getDescripcion());
    }

    public CategoriaEntity toEntity(Categoria domain) {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(domain.id());
        entity.setNombre(domain.nombre());
        entity.setDescripcion(domain.descripcion());
        return entity;
    }
}
