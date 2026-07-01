package com.agroinventario.application.usecase.inventario;

import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.ports.input.inventario.ListarMovimientosInventarioUseCase;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarMovimientosInventarioUseCaseImpl implements ListarMovimientosInventarioUseCase {

    private final MovimientoInventarioRepositoryPort movimientoRepository;

    public ListarMovimientosInventarioUseCaseImpl(MovimientoInventarioRepositoryPort movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public List<MovimientoInventario> ejecutar(Long productoId) {
        if (productoId == null) {
            return movimientoRepository.findAll();
        }
        return movimientoRepository.findByProductoId(productoId);
    }
}
