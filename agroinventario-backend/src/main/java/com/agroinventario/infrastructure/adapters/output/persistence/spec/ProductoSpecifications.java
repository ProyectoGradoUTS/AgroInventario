package com.agroinventario.infrastructure.adapters.output.persistence.spec;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.infrastructure.adapters.output.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.domain.Specification;

public final class ProductoSpecifications {

    private ProductoSpecifications() {
    }

    public static Specification<ProductoEntity> conFiltros(
            EstadoGeneral estado, Long categoriaId, String nombre) {
        return (root, query, cb) -> {
            if (query != null) {
                root.fetch("categoria", jakarta.persistence.criteria.JoinType.LEFT);
                query.distinct(true);
            }
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }
            if (categoriaId != null) {
                predicates.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
