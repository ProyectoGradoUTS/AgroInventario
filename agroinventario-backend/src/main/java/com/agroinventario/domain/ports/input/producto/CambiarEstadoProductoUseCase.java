package com.agroinventario.domain.ports.input.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;

public interface CambiarEstadoProductoUseCase {

    Producto ejecutar(Long id, EstadoGeneral estado);
}
