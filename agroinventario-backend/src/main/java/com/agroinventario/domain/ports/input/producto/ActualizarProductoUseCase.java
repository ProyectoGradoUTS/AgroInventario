package com.agroinventario.domain.ports.input.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ActualizarProductoUseCase {

    Producto ejecutar(
            Long id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stockMinimo,
            LocalDate fechaVencimiento,
            EstadoGeneral estado
    );
}
