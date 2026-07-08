package com.agroinventario.application.usecase.producto;

import com.agroinventario.domain.exception.ResourceNotFoundException;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasProductoUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
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
    private final ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;

    public ActualizarProductoUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase) {
        this.productoRepository = productoRepository;
        this.procesarAlertasProductoUseCase = procesarAlertasProductoUseCase;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
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

        Producto guardado = productoRepository.save(actualizado);
        procesarAlertasProductoUseCase.ejecutar(guardado);
        registrarAuditoriaUseCase.ejecutar(
                EntidadAuditoria.PRODUCTO,
                guardado.id(),
                TipoAccionAuditoria.ACTUALIZAR,
                "Producto '%s' actualizado".formatted(guardado.nombre())
        );
        return guardado;
    }
}
