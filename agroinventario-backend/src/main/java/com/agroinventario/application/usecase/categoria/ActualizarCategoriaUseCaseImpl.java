package com.agroinventario.application.usecase.categoria;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Categoria;
import com.agroinventario.domain.ports.input.categoria.ActualizarCategoriaUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarCategoriaUseCaseImpl implements ActualizarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepository;

    public ActualizarCategoriaUseCaseImpl(CategoriaRepositoryPort categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Categoria ejecutar(Long id, String nombre, String descripcion) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));

        categoriaRepository.findByNombre(nombre)
                .filter(c -> !c.id().equals(id))
                .ifPresent(c -> {
                    throw new BusinessRuleException("Ya existe una categoría con el nombre: " + nombre);
                });

        Categoria actualizada = new Categoria(existente.id(), nombre, descripcion);
        return categoriaRepository.save(actualizada);
    }
}
