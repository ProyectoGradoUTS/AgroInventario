package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.producto.ActualizarProductoUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class ActualizarProductoUseCaseImpl implements ActualizarProductoUseCase {

    private final ProductoRepositoryPort productoRepository;

    public ActualizarProductoUseCaseImpl(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto ejecutar(
            Long id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stockMinimo,
            LocalDate fechaVencimiento,
            EstadoGeneral estado) {

        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        Producto actualizado = existente.conDatos(
                nombre, descripcion, precio, stockMinimo, fechaVencimiento, estado);

        return productoRepository.save(actualizado);
    }
}
