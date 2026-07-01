package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.MovimientoInventario;

import java.util.List;

public interface MovimientoInventarioRepositoryPort {

    MovimientoInventario save(MovimientoInventario movimiento);

    List<MovimientoInventario> findByProductoId(Long productoId);

    List<MovimientoInventario> findAll();

    boolean existsByProductoId(Long productoId);
}
