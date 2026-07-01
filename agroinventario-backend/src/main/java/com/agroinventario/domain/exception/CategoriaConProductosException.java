package com.agroinventario.domain.exception;

public class CategoriaConProductosException extends BusinessRuleException {

    public CategoriaConProductosException(Long categoriaId) {
        super("No se puede eliminar la categoría %d porque tiene productos asociados".formatted(categoriaId));
    }
}
