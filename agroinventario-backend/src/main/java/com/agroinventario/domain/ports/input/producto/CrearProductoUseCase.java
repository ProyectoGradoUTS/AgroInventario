package com.agroinventario.domain.ports.input.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CrearProductoUseCase {

    Producto ejecutar(
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stockActual,
            int stockMinimo,
            LocalDate fechaVencimiento,
            Long categoriaId,
            EstadoGeneral estado
    );
}
