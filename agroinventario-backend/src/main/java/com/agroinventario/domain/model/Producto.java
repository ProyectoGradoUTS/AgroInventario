package com.agroinventario.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Producto(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        int stockActual,
        int stockMinimo,
        LocalDate fechaVencimiento,
        Long categoriaId,
        String categoriaNombre,
        EstadoGeneral estado,
        LocalDateTime fechaCreacion
) {

    public Producto conStock(int nuevoStock) {
        return new Producto(id, nombre, descripcion, precio, nuevoStock, stockMinimo,
                fechaVencimiento, categoriaId, categoriaNombre, estado, fechaCreacion);
    }

    public Producto conDatos(String nombre, String descripcion, BigDecimal precio,
                             int stockMinimo, LocalDate fechaVencimiento, EstadoGeneral estado) {
        return new Producto(id, nombre, descripcion, precio, stockActual, stockMinimo,
                fechaVencimiento, categoriaId, categoriaNombre, estado, fechaCreacion);
    }

    public boolean tieneStockSuficiente(int cantidad) {
        return stockActual >= cantidad;
    }

    public boolean stockBajo() {
        return stockActual <= stockMinimo;
    }

    public boolean estaActivo() {
        return estado == EstadoGeneral.ACTIVO;
    }
}
