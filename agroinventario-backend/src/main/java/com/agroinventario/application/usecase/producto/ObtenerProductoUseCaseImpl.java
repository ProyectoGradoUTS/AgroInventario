package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.producto.ObtenerProductoUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerProductoUseCaseImpl implements ObtenerProductoUseCase {

    private final ProductoRepositoryPort productoRepository;

    public ObtenerProductoUseCaseImpl(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto ejecutar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }
}
