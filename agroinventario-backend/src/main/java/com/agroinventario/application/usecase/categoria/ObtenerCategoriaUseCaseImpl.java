package com.agroinventario.application.usecase.categoria;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Categoria;
import com.agroinventario.domain.ports.input.categoria.ObtenerCategoriaUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerCategoriaUseCaseImpl implements ObtenerCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepository;

    public ObtenerCategoriaUseCaseImpl(CategoriaRepositoryPort categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Categoria ejecutar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }
}
