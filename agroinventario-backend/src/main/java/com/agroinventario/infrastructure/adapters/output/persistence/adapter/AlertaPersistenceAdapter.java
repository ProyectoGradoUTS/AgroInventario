package com.agroinventario.infrastructure.adapters.output.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.TipoAlerta;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.infrastructure.adapters.output.persistence.mapper.AlertaEntityMapper;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.AlertaJpaRepository;
import com.agroinventario.infrastructure.adapters.output.persistence.repository.ProductoJpaRepository;

@Component
public class AlertaPersistenceAdapter implements AlertaRepositoryPort {

    private final AlertaJpaRepository jpaRepository;
    private final ProductoJpaRepository productoJpaRepository;
    private final AlertaEntityMapper mapper;

    public AlertaPersistenceAdapter(
            AlertaJpaRepository jpaRepository,
            ProductoJpaRepository productoJpaRepository,
            AlertaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.productoJpaRepository = productoJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Alerta save(Alerta alerta) {
        var producto = productoJpaRepository.findById(alerta.productoId())
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado"));
        var saved = jpaRepository.save(mapper.toEntity(alerta, producto));
        return jpaRepository.findWithProductoById(saved.getId()).map(mapper::toDomain).orElseThrow();
    }

    @Override
    public List<Alerta> findAll() {
        return jpaRepository.findAllByOrderByFechaGeneracionDesc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Alerta> findByEstado(EstadoAlerta estado) {
        return jpaRepository.findByEstadoOrderByFechaGeneracionDesc(estado).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Alerta> findById(Long id) {
        return jpaRepository.findWithProductoById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Alerta> findPendienteByProductoAndTipo(Long productoId, TipoAlerta tipoAlerta) {
        return jpaRepository.findByProductoIdAndTipoAlertaAndEstado(productoId, tipoAlerta, EstadoAlerta.PENDIENTE)
                .map(mapper::toDomain);
    }
}
