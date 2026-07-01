package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.MovimientoEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.MovimientoInventarioJpaRepository;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.ProductoJpaRepository;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoInventarioPersistenceAdapter implements MovimientoInventarioRepositoryPort {

    private final MovimientoInventarioJpaRepository jpaRepository;
    private final ProductoJpaRepository productoJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;
    private final MovimientoEntityMapper mapper;

    public MovimientoInventarioPersistenceAdapter(
            MovimientoInventarioJpaRepository jpaRepository,
            ProductoJpaRepository productoJpaRepository,
            UsuarioJpaRepository usuarioJpaRepository,
            MovimientoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.productoJpaRepository = productoJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public MovimientoInventario save(MovimientoInventario movimiento) {
        var producto = productoJpaRepository.findById(movimiento.productoId())
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado"));
        var usuario = usuarioJpaRepository.findById(movimiento.usuarioId())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        var saved = jpaRepository.save(mapper.toEntity(movimiento, producto, usuario));
        return jpaRepository.findById(saved.getId())
                .map(mapper::toDomain)
                .orElseThrow();
    }

    @Override
    public List<MovimientoInventario> findByProductoId(Long productoId) {
        return jpaRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<MovimientoInventario> findAll() {
        return jpaRepository.findAllByOrderByFechaMovimientoDesc()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByProductoId(Long productoId) {
        return jpaRepository.existsByProductoId(productoId);
    }
}
