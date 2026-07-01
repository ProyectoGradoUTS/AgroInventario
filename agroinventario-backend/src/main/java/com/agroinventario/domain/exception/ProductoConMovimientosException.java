package com.agroinventario.domain.exception;

public class ProductoConMovimientosException extends BusinessRuleException {

    public ProductoConMovimientosException(Long productoId) {
        super("No se puede eliminar el producto %d porque tiene movimientos de inventario".formatted(productoId));
    }
}
