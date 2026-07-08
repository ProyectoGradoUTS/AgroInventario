package com.agroinventario.application.usecase.inventario;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.MovimientoInventario;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.model.TipoMovimiento;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasProductoUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.input.inventario.RegistrarMovimientoInventarioUseCase;
import com.agroinventario.domain.ports.output.CurrentUserPort;
import com.agroinventario.domain.ports.output.MovimientoInventarioRepositoryPort;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import com.agroinventario.domain.ports.output.UsuarioRepositoryPort;
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
    private final CurrentUserPort currentUserPort;
    private final InventarioDomainService inventarioDomainService;
    private final ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public RegistrarMovimientoInventarioUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            MovimientoInventarioRepositoryPort movimientoRepository,
            UsuarioRepositoryPort usuarioRepository,
            CurrentUserPort currentUserPort,
            InventarioDomainService inventarioDomainService,
            ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.currentUserPort = currentUserPort;
        this.inventarioDomainService = inventarioDomainService;
        this.procesarAlertasProductoUseCase = procesarAlertasProductoUseCase;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
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
        procesarAlertasProductoUseCase.ejecutar(productoActualizado);

        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.INVENTARIO,
                guardado.id(),
                TipoAccionAuditoria.MOVIMIENTO_INVENTARIO,
                "%s de %d unidad(es) en producto '%s' (id=%d)".formatted(
                        tipoMovimiento, cantidad, producto.nombre(), producto.id())
        );

        return guardado;
    }
}
