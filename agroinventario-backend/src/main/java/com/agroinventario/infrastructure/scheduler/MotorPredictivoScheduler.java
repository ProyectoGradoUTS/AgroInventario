package com.agroinventario.infrastructure.scheduler;

import com.agroinventario.domain.ports.input.dashboard.PrediccionInventarioUseCase;
import com.agroinventario.domain.ports.input.dashboard.SincronizarHistoricoInventarioUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class MotorPredictivoScheduler {

    private static final Logger log = LoggerFactory.getLogger(MotorPredictivoScheduler.class);

    private final SincronizarHistoricoInventarioUseCase sincronizarHistoricoInventarioUseCase;
    private final PrediccionInventarioUseCase prediccionInventarioUseCase;

    public MotorPredictivoScheduler(
            SincronizarHistoricoInventarioUseCase sincronizarHistoricoInventarioUseCase,
            PrediccionInventarioUseCase prediccionInventarioUseCase) {
        this.sincronizarHistoricoInventarioUseCase = sincronizarHistoricoInventarioUseCase;
        this.prediccionInventarioUseCase = prediccionInventarioUseCase;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void alArrancar() {
        ejecutar("arranque");
    }

    @Scheduled(cron = "${app.ia.cron-prediccion:0 15 6 * * *}", zone = "America/Bogota")
    public void diario() {
        ejecutar("programado");
    }

    private void ejecutar(String origen) {
        try {
            int productos = sincronizarHistoricoInventarioUseCase.ejecutar();
            int predicciones = prediccionInventarioUseCase.ejecutar().size();
            log.info("Motor predictivo ({}) sincronizó {} productos y persistió {} proyecciones", origen, productos, predicciones);
        } catch (Exception ex) {
            log.warn("Motor predictivo ({}) no pudo ejecutarse: {}", origen, ex.getMessage());
        }
    }
}
