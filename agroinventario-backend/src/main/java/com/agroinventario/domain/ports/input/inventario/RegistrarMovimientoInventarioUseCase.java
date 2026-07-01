package com.agroinventario.domain.ports.input.inventario;

import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.model.TipoMovimiento;

public interface RegistrarMovimientoInventarioUseCase {

    MovimientoInventario ejecutar(
            Long productoId,
            TipoMovimiento tipoMovimiento,
            int cantidad,
            String descripcion
    );
}
