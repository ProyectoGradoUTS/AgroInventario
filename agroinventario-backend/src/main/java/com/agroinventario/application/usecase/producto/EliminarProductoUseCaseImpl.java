package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ProductoConMovimientosException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.ports.input.producto.EliminarProductoUseCase;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarProductoUseCaseImpl implements EliminarProductoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final MovimientoInventarioRepositoryPort movimientoRepository;

    public EliminarProductoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            MovimientoInventarioRepositoryPort movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public void ejecutar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        if (movimientoRepository.existsByProductoId(id)) {
            throw new ProductoConMovimientosException(id);
        }
        productoRepository.deleteById(id);
    }
}
