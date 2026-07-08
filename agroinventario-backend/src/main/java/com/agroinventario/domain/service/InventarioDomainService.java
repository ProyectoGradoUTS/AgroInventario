package com.agroinventario.domain.service;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.StockInsuficienteException;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoMovimiento;

/**
 * Reglas de negocio del inventario (sin dependencias de infraestructura).
 */
public class InventarioDomainService {

    public void validarProductoActivo(Producto producto) {
        if (!producto.estaActivo()) {
            throw new BusinessRuleException(
                    "El producto '%s' está inactivo y no admite movimientos".formatted(producto.nombre()));
        }
    }

    public void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor a cero");
        }
    }

    public int calcularNuevoStock(Producto producto, TipoMovimiento tipo, int cantidad) {
        validarProductoActivo(producto);
        validarCantidad(cantidad);

        return switch (tipo) {
            case ENTRADA -> producto.stockActual() + cantidad;
            case SALIDA -> {
                if (!producto.tieneStockSuficiente(cantidad)) {
                    throw new StockInsuficienteException(producto.nombre(), producto.stockActual(), cantidad);
                }
                yield producto.stockActual() - cantidad;
            }
        };
    }
}
