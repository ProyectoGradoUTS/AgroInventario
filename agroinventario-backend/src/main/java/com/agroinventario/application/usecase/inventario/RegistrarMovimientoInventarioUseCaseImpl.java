package com.agroinventario.application.usecase.inventario;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAlerta;
import com.agroinventario.domain.model.TipoMovimiento;
import com.agroinventario.domain.ports.input.inventario.RegistrarMovimientoInventarioUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.ports.output.CurrentUserPort;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
import com.agroinventario.domain.service.AlertaDomainService;
import com.agroinventario.domain.service.InventarioDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class RegistrarMovimientoInventarioUseCaseImpl implements RegistrarMovimientoInventarioUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final MovimientoInventarioRepositoryPort movimientoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final AlertaRepositoryPort alertaRepository;
    private final CurrentUserPort currentUserPort;
    private final InventarioDomainService inventarioDomainService;
    private final AlertaDomainService alertaDomainService;

    public RegistrarMovimientoInventarioUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            MovimientoInventarioRepositoryPort movimientoRepository,
            UsuarioRepositoryPort usuarioRepository,
            AlertaRepositoryPort alertaRepository,
            CurrentUserPort currentUserPort,
            InventarioDomainService inventarioDomainService,
            AlertaDomainService alertaDomainService) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alertaRepository = alertaRepository;
        this.currentUserPort = currentUserPort;
        this.inventarioDomainService = inventarioDomainService;
        this.alertaDomainService = alertaDomainService;
    }

    @Override
    public MovimientoInventario ejecutar(
            Long productoId,
            TipoMovimiento tipoMovimiento,
            int cantidad,
            String descripcion) {

        Long usuarioId = currentUserPort.getUserId();

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productoId));

        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        int nuevoStock = inventarioDomainService.calcularNuevoStock(producto, tipoMovimiento, cantidad);
        Producto productoActualizado = producto.conStock(nuevoStock);
        productoRepository.save(productoActualizado);

        MovimientoInventario movimiento = new MovimientoInventario(
                null,
                producto.id(),
                producto.nombre(),
                tipoMovimiento,
                cantidad,
                descripcion,
                usuario.id(),
                usuario.nombre(),
                LocalDateTime.now()
        );

        MovimientoInventario guardado = movimientoRepository.save(movimiento);
        procesarAlertaStockBajo(productoActualizado);

        return guardado;
    }

    private void procesarAlertaStockBajo(Producto producto) {
        if (!alertaDomainService.requiereAlertaStockBajo(producto)) {
            return;
        }

        alertaRepository.findPendienteByProductoAndTipo(producto.id(), TipoAlerta.STOCK_BAJO)
                .ifPresentOrElse(
                        existente -> { },
                        () -> alertaRepository.save(alertaDomainService.crearAlertaStockBajo(producto))
                );
    }
}
