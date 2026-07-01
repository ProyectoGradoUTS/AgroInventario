package com.agroinventario.domain.ports.input.inventario;

import com.agroinventario.domain.model.MovimientoInventario;

import java.util.List;

public interface ListarMovimientosInventarioUseCase {

    List<MovimientoInventario> ejecutar(Long productoId);
}
