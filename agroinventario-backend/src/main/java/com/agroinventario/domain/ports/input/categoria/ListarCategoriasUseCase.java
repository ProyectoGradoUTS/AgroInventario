package com.agroinventario.domain.ports.input.categoria;

import com.agroinventario.domain.model.Categoria;

import java.util.List;

public interface ListarCategoriasUseCase {

    List<Categoria> ejecutar();
}
