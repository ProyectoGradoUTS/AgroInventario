package com.agroinventario.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AlertaTest {

    @Test
    void permiteTransicionDePendienteALeida() {
        Alerta alerta = alertaConEstado(EstadoAlerta.PENDIENTE);

        assertThat(alerta.puedeTransicionarA(EstadoAlerta.LEIDA)).isTrue();
    }

    @Test
    void noPermiteTransicionDeResueltaAPendiente() {
        Alerta alerta = alertaConEstado(EstadoAlerta.RESUELTA);

        assertThat(alerta.puedeTransicionarA(EstadoAlerta.PENDIENTE)).isFalse();
    }

    private Alerta alertaConEstado(EstadoAlerta estado) {
        return new Alerta(
                1L,
                10L,
                "Urea",
                TipoAlerta.STOCK_BAJO,
                "Stock bajo",
                estado,
                LocalDateTime.now()
        );
    }
}
