package com.agroinventario.application.usecase.alerta;

import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasProductoUseCase;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasVencimientoProgramadasUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.output.ProductoRepositoryPort;
import com.agroinventario.infrastructure.config.AlertasProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ProcesarAlertasVencimientoProgramadasUseCaseImpl implements ProcesarAlertasVencimientoProgramadasUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;
    private final int diasAnticipacionVencimiento;

    public ProcesarAlertasVencimientoProgramadasUseCaseImpl(
            ProductoRepositoryPort productoRepository,
            ProcesarAlertasProductoUseCase procesarAlertasProductoUseCase,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase,
            AlertasProperties alertasProperties) {
        this.productoRepository = productoRepository;
        this.procesarAlertasProductoUseCase = procesarAlertasProductoUseCase;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
        this.diasAnticipacionVencimiento = alertasProperties.diasAnticipacionVencimiento();
    }

    @Override
    public int ejecutar() {
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacionVencimiento);
        var productos = productoRepository.findActivosConVencimientoHasta(fechaLimite);

        productos.forEach(procesarAlertasProductoUseCase::ejecutar);

        if (!productos.isEmpty()) {
            registrarAuditoriaUseCase.ejecutar(
                    EntidadAuditoria.ALERTA,
                    null,
                    TipoAccionAuditoria.PROCESO_AUTOMATICO,
                    "Proceso programado de vencimiento revisó %d producto(s)".formatted(productos.size())
            );
        }

        return productos.size();
    }
}
