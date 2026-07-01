package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.MovimientoInventarioEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.ProductoEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class MovimientoEntityMapper {

    public MovimientoInventario toDomain(MovimientoInventarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MovimientoInventario(
                entity.getId(),
                entity.getProducto().getId(),
                entity.getProducto().getNombre(),
                entity.getTipoMovimiento(),
                entity.getCantidad(),
                entity.getDescripcion(),
                entity.getUsuario().getId(),
                entity.getUsuario().getNombre(),
                entity.getFechaMovimiento()
        );
    }

    public MovimientoInventarioEntity toEntity(
            MovimientoInventario domain,
            ProductoEntity producto,
            UsuarioEntity usuario) {
        MovimientoInventarioEntity entity = new MovimientoInventarioEntity();
        entity.setId(domain.id());
        entity.setProducto(producto);
        entity.setTipoMovimiento(domain.tipoMovimiento());
        entity.setCantidad(domain.cantidad());
        entity.setDescripcion(domain.descripcion());
        entity.setUsuario(usuario);
        entity.setFechaMovimiento(domain.fechaMovimiento());
        return entity;
    }
}
