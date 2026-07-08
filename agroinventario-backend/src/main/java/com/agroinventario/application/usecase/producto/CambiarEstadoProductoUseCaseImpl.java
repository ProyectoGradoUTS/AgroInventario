package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasProductoUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.input.producto.CambiarEstadoProductoUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CambiarEstadoProductoUseCaseImpl implements CambiarEstadoProductoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public CambiarEstadoProductoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.productoRepository = productoRepository;
        this.procesarAlertasProductoUseCase = procesarAlertasProductoUseCase;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
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

        Producto guardado = productoRepository.save(actualizado);
        procesarAlertasProductoUseCase.ejecutar(guardado);
        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.PRODUCTO,
                guardado.id(),
                TipoAccionAuditoria.CAMBIAR_ESTADO,
                "Producto '%s' cambió a estado %s".formatted(guardado.nombre(), estado)
        );
        return guardado;
    }
}
