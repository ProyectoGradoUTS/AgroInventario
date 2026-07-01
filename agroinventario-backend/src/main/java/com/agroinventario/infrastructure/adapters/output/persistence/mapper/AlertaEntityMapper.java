package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.AlertaEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.ProductoEntity;
import org.springframework.stereotype.Component;

@Component
public class AlertaEntityMapper {

    public Alerta toDomain(AlertaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Alerta(
                entity.getId(),
                entity.getProducto().getId(),
                entity.getProducto().getNombre(),
                entity.getTipoAlerta(),
                entity.getMensaje(),
                entity.getEstado(),
                entity.getFechaGeneracion()
        );
    }

    public AlertaEntity toEntity(Alerta domain, ProductoEntity producto) {
        AlertaEntity entity = new AlertaEntity();
        entity.setId(domain.id());
        entity.setProducto(producto);
        entity.setTipoAlerta(domain.tipoAlerta());
        entity.setMensaje(domain.mensaje());
        entity.setEstado(domain.estado());
        entity.setFechaGeneracion(domain.fechaGeneracion());
        return entity;
    }
}
