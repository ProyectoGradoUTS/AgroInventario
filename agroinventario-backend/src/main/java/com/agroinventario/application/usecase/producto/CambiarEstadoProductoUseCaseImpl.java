package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.producto.CambiarEstadoProductoUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoProductoUseCaseImpl implements CambiarEstadoProductoUseCase {

    private final ProductoRepositoryPort productoRepository;

    public CambiarEstadoProductoUseCaseImpl(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto ejecutar(Long id, EstadoGeneral estado) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        Producto actualizado = existente.conDatos(
                existente.nombre(),
                existente.descripcion(),
                existente.precio(),
                existente.stockMinimo(),
                existente.fechaVencimiento(),
                estado
        );

        return productoRepository.save(actualizado);
    }
}
