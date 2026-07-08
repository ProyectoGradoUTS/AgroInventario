package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ProductoConMovimientosException;
import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
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
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public EliminarProductoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            MovimientoInventarioRepositoryPort movimientoRepository,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
    }

    @Override
    public void ejecutar(Long id) {
        var producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (movimientoRepository.existsByProductoId(id)) {
            throw new ProductoConMovimientosException(id);
        }

        productoRepository.deleteById(id);
        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.PRODUCTO,
                id,
                TipoAccionAuditoria.ELIMINAR,
                "Producto '%s' eliminado".formatted(producto.nombre())
        );
    }
}
