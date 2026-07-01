package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.producto.CrearProductoUseCase;
import com.agroinventario.domain.ports.output.CategoriaRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class CrearProductoUseCaseImpl implements CrearProductoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final CategoriaRepositoryPort categoriaRepository;

    public CrearProductoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            CategoriaRepositoryPort categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Producto ejecutar(
            String nombre,
            String descripcion,
            BigDecimal precio,
            int stockActual,
            int stockMinimo,
            LocalDate fechaVencimiento,
            Long categoriaId,
            EstadoGeneral estado) {

        var categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", categoriaId));

        EstadoGeneral estadoFinal = estado != null ? estado : EstadoGeneral.ACTIVO;

        Producto producto = new Producto(
                null,
                nombre,
                descripcion,
                precio,
                stockActual,
                stockMinimo,
                fechaVencimiento,
                categoria.id(),
                categoria.nombre(),
                estadoFinal,
                LocalDateTime.now()
        );

        return productoRepository.save(producto);
    }
}
