package com.agroinventario.domain.ports.input.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.PageResult;
import com.agroinventario.domain.model.Producto;

public interface ListarProductosPaginadoUseCase {

    PageResult<Producto> ejecutar(
            EstadoGeneral estado,
            Long categoriaId,
            String nombre,
            int page,
            int size
    );
}
