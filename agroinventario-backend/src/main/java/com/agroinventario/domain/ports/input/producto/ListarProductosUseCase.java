package com.agroinventario.domain.ports.input.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;

import java.util.List;

public interface ListarProductosUseCase {

    List<Producto> ejecutar(EstadoGeneral estado);
}
