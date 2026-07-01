package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.PageResult;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.ports.input.producto.ListarProductosPaginadoUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListarProductosPaginadoUseCaseImpl implements ListarProductosPaginadoUseCase {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductoRepositoryPort productoRepository;

    public ListarProductosPaginadoUseCaseImpl(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public PageResult<Producto> ejecutar(
            EstadoGeneral estado, Long categoriaId, String nombre, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : Math.min(size, MAX_PAGE_SIZE);
        String safeNombre = nombre != null && nombre.isBlank() ? null : nombre;

        return productoRepository.findPaginado(estado, categoriaId, safeNombre, safePage, safeSize);
    }
}
