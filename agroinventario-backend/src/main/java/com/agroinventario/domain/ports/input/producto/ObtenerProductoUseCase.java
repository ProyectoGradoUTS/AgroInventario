package com.agroinventario.domain.ports.input.producto;

import com.agroinventario.domain.model.Producto;

public interface ObtenerProductoUseCase {

    Producto ejecutar(Long id);
}
