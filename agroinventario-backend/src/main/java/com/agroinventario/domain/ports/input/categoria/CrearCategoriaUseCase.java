package com.agroinventario.domain.ports.input.categoria;

import com.agroinventario.domain.model.Categoria;

public interface CrearCategoriaUseCase {

    Categoria ejecutar(String nombre, String descripcion);
}
