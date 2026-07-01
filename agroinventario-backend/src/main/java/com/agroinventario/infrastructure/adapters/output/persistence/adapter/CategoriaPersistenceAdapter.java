package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.Categoria;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.CategoriaEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.CategoriaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoriaPersistenceAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaEntityMapper mapper;

    public CategoriaPersistenceAdapter(CategoriaJpaRepository jpaRepository, CategoriaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Categoria save(Categoria categoria) {
        var saved = jpaRepository.save(mapper.toEntity(categoria));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public List<Categoria> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
