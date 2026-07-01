package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.PageResult;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.ProductoEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.CategoriaJpaRepository;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.ProductoJpaRepository;
import com.agroinventario.infrastructure.adapters.output.persistence.spec.ProductoSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoPersistenceAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository jpaRepository;
    private final CategoriaJpaRepository categoriaJpaRepository;
    private final ProductoEntityMapper mapper;

    public ProductoPersistenceAdapter(
            ProductoJpaRepository jpaRepository,
            CategoriaJpaRepository categoriaJpaRepository,
            ProductoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.categoriaJpaRepository = categoriaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Producto save(Producto producto) {
        var categoria = categoriaJpaRepository.findById(producto.categoriaId())
                .orElseThrow(() -> new IllegalStateException("Categoría no encontrada: " + producto.categoriaId()));
        var entity = mapper.toEntity(producto, categoria);
        var saved = jpaRepository.save(entity);
        return jpaRepository.findWithCategoriaById(saved.getId())
                .map(mapper::toDomain)
                .orElseThrow();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return jpaRepository.findWithCategoriaById(id).map(mapper::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return jpaRepository.findAllByOrderByNombreAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Producto> findByEstado(EstadoGeneral estado) {
        return jpaRepository.findByEstadoOrderByNombreAsc(estado).stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<Producto> findPaginado(
            EstadoGeneral estado, Long categoriaId, String nombre, int page, int size) {
        Specification<com.agroinventario.infrastructure.adapters.output.persistence.entity.ProductoEntity> spec =
                ProductoSpecifications.conFiltros(estado, categoriaId, nombre);
        var pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        var result = jpaRepository.findAll(spec, pageable);

        return new PageResult<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public long countByCategoriaId(Long categoriaId) {
        return jpaRepository.countByCategoriaId(categoriaId);
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
