package com.agroinventario.infrastructure.scheduler;

import com.agroinventario.domain.ports.input.alerta.ProcesarAlertasVencimientoProgramadasUseCase;
import com.agroinventario.infrastructure.config.AlertasProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertaVencimientoScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaVencimientoScheduler.class);

    private final ProcesarAlertasVencimientoProgramadasUseCase procesarAlertasVencimientoUseCase;

    public AlertaVencimientoScheduler(ProcesarAlertasVencimientoProgramadasUseCase procesarAlertasVencimientoUseCase) {
        this.procesarAlertasVencimientoUseCase = procesarAlertasVencimientoUseCase;
    }

    @Scheduled(cron = "${app.alertas.cron-vencimiento:0 0 6 * * *}", zone = "America/Bogota")
    public void procesarAlertasVencimiento() {
        int productosRevisados = procesarAlertasVencimientoUseCase.ejecutar();
        log.info("Proceso programado de alertas de vencimiento finalizado. Productos revisados: {}", productosRevisados);
    }
}
