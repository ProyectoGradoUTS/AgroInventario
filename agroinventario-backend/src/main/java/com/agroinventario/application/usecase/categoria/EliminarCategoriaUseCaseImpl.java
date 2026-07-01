package com.agroinventario.application.usecase.categoria;

import com.agroinventario.domain.exception.CategoriaConProductosException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.ports.input.categoria.EliminarCategoriaUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarCategoriaUseCaseImpl implements EliminarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepository;
    private final ProductoRepositoryPort productoRepository;

    public EliminarCategoriaUseCaseImpl(
            CategoriaRepositoryPort categoriaRepository,
            ProductoRepositoryPort productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void ejecutar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría", id);
        }
        if (productoRepository.countByCategoriaId(id) > 0) {
            throw new CategoriaConProductosException(id);
        }
        categoriaRepository.deleteById(id);
    }
}
