package com.agroinventario.application.usecase.categoria;

import com.agroinventario.domain.exception.BusinessRuleException;
import com.agroinventario.domain.model.Categoria;
import com.agroinventario.domain.ports.input.categoria.CrearCategoriaUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearCategoriaUseCaseImpl implements CrearCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepository;

    public CrearCategoriaUseCaseImpl(CategoriaRepositoryPort categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Categoria ejecutar(String nombre, String descripcion) {
        categoriaRepository.findByNombre(nombre).ifPresent(c -> {
            throw new BusinessRuleException("Ya existe una categoría con el nombre: " + nombre);
        });

        Categoria nueva = new Categoria(null, nombre, descripcion);
        return categoriaRepository.save(nueva);
    }
}
