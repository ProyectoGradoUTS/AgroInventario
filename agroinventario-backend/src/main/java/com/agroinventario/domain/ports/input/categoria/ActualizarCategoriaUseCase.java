package com.agroinventario.domain.ports.input.categoria;

import com.agroinventario.domain.model.Categoria;

public interface ActualizarCategoriaUseCase {

    Categoria ejecutar(Long id, String nombre, String descripcion);
}
