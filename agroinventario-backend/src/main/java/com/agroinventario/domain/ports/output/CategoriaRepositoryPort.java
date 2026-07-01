package com.agroinventario.domain.ports.output;

import com.agroinventario.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {

    Categoria save(Categoria categoria);

    Optional<Categoria> findById(Long id);

    Optional<Categoria> findByNombre(String nombre);

    List<Categoria> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
