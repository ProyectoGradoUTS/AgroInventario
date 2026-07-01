package com.agroinventario.infrastructure.adapters.output.persistence.mapper;

import com.agroinventario.domain.model.Producto;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.CategoriaEntity;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.ProductoEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductoEntityMapper {

    public Producto toDomain(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }
        String categoriaNombre = entity.getCategoria() != null ? entity.getCategoria().getNombre() : null;
        Long categoriaId = entity.getCategoria() != null ? entity.getCategoria().getId() : null;

        return new Producto(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecio(),
                entity.getStockActual(),
                entity.getStockMinimo(),
                entity.getFechaVencimiento(),
                categoriaId,
                categoriaNombre,
                entity.getEstado(),
                entity.getFechaCreacion()
        );
    }

    public ProductoEntity toEntity(Producto domain, CategoriaEntity categoriaEntity) {
        ProductoEntity entity = new ProductoEntity();
        entity.setId(domain.id());
        entity.setNombre(domain.nombre());
        entity.setDescripcion(domain.descripcion());
        entity.setPrecio(domain.precio());
        entity.setStockActual(domain.stockActual());
        entity.setStockMinimo(domain.stockMinimo());
        entity.setFechaVencimiento(domain.fechaVencimiento());
        entity.setCategoria(categoriaEntity);
        entity.setEstado(domain.estado());
        if (domain.fechaCreacion() != null) {
            entity.setFechaCreacion(domain.fechaCreacion());
        }
        return entity;
    }
}
