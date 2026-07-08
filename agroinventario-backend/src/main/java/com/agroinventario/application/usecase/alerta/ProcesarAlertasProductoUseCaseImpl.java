package com.agroinventario.application.usecase.alerta;

import com.agroinventario.domain.model.Alerta;
import com.agroinventario.domain.model.EntidadAuditoria;
import com.agroinventario.domain.model.EstadoAlerta;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.TipoAccionAuditoria;
import com.agroinventario.domain.model.TipoAlerta;
import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasProductoUseCase;
import com.agroinventario.domain.ports.input.auditoria.RegistrarAuditoriaUseCase;
import com.agroinventario.domain.ports.output.AlertaRepositoryPort;
import com.agroinventario.domain.service.AlertaDomainService;
import com.agroinventario.infrastructure.config.AlertasProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcesarAlertasProductoUseCaseImpl implements ProcesarAlertasProductoUseCase {

    private final AlertaRepositoryPort alertaRepository;
    private final AlertaDomainService alertaDomainService;
    private final RegistrarAuditoriaUseCase registrarAuditoriaUseCase;
    private final int diasAnticipacionVencimiento;

    public ProcesarAlertasProductoUseCaseImpl(
            AlertaRepositoryPort alertaRepository,
            AlertaDomainService alertaDomainService,
            RegistrarAuditoriaUseCase registrarAuditoriaUseCase,
            AlertasProperties alertasProperties) {
        this.alertaRepository = alertaRepository;
        this.alertaDomainService = alertaDomainService;
        this.registrarAuditoriaUseCase = registrarAuditoriaUseCase;
        this.diasAnticipacionVencimiento = alertasProperties.diasAnticipacionVencimiento();
    }

    @Override
    public void ejecutar(Producto producto) {
        if (!producto.estaActivo()) {
            resolverPendiente(producto, TipoAlerta.STOCK_BAJO);
            resolverPendiente(producto, TipoAlerta.VENCIMIENTO_PROXIMO);
            return;
        }

        procesarTipoAlerta(
                producto,
                TipoAlerta.STOCK_BAJO,
                alertaDomainService.requiereAlertaStockBajo(producto),
                alertaDomainService.crearAlertaStockBajo(producto)
        );

        procesarTipoAlerta(
                producto,
                TipoAlerta.VENCIMIENTO_PROXIMO,
                alertaDomainService.requiereAlertaVencimiento(producto, diasAnticipacionVencimiento),
                alertaDomainService.crearAlertaVencimientoProximo(producto)
        );
    }

    private void procesarTipoAlerta(
            Producto producto,
            TipoAlerta tipo,
            boolean requiereAlerta,
            Alerta alertaNueva) {
        if (requiereAlerta) {
            alertaRepository.findPendienteByProductoAndTipo(producto.id(), tipo)
                    .ifPresentOrElse(
                            existente -> { },
                            () -> {
                                Alerta guardada = alertaRepository.save(alertaNueva);
                                registrarAuditoriaUseCase.ejecutar(
                                        EntidadAuditoria.ALERTA,
                                        guardada.id(),
                                        TipoAccionAuditoria.ALERTA_GENERADA,
                                        guardada.mensaje()
                                );
                            }
                    );
            return;
        }

        resolverPendiente(producto, tipo);
    }

    private void resolverPendiente(Producto producto, TipoAlerta tipo) {
        alertaRepository.findPendienteByProductoAndTipo(producto.id(), tipo)
                .ifPresent(alerta -> {
                    Alerta resuelta = alertaRepository.save(alerta.conEstado(EstadoAlerta.RESUELTA));
                    registrarAuditoriaUseCase.ejecutar(
                            EntidadAuditoria.ALERTA,
                            resuelta.id(),
                            TipoAccionAuditoria.ALERTA_ACTUALIZADA,
                            "Alerta %s resuelta automáticamente para producto '%s'"
                                    .formatted(tipo, producto.nombre())
                    );
                });
    }
}
