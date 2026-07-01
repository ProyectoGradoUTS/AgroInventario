package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.producto.ListarProductosUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarProductosUseCaseImpl implements ListarProductosUseCase {

    private final ProductoRepositoryPort productoRepository;

    public ListarProductosUseCaseImpl(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> ejecutar(EstadoGeneral estado) {
        if (estado == null) {
            return productoRepository.findAll();
        }
        return productoRepository.findByEstado(estado);
    }
}
