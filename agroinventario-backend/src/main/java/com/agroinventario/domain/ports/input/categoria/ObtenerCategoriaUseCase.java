package com.agroinventario.domain.ports.input.categoria;

import com.agroinventario.domain.model.Categoria;

public interface ObtenerCategoriaUseCase {

    Categoria ejecutar(Long id);
}
