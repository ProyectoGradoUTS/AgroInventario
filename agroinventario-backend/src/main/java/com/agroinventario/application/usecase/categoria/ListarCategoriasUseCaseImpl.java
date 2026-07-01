package com.agroinventario.application.usecase.categoria;

import com.agroinventario.domain.model.Categoria;
import com.agroinventario.domain.ports.input.categoria.ListarCategoriasUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarCategoriasUseCaseImpl implements ListarCategoriasUseCase {

    private final CategoriaRepositoryPort categoriaRepository;

    public ListarCategoriasUseCaseImpl(CategoriaRepositoryPort categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> ejecutar() {
        return categoriaRepository.findAll();
    }
}
