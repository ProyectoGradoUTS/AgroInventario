package com.agroinventario.domain.service;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AlertaDomainServiceTest {

    private AlertaDomainService service;

    @BeforeEach
    void setUp() {
        service = new AlertaDomainService();
    }

    @Test
    void requiereAlertaStockBajo_cuandoStockEsIgualAlMinimo() {
        Producto producto = productoBase(5, 5, LocalDate.now().plusDays(60));

        assertThat(service.requiereAlertaStockBajo(producto)).isTrue();
    }

    @Test
    void noRequiereAlertaStockBajo_cuandoProductoInactivo() {
        Producto producto = new Producto(
                1L, "Urea", "desc", BigDecimal.TEN, 2, 5,
                LocalDate.now().plusDays(60), 1L, "Granos", EstadoGeneral.INACTIVO, LocalDateTime.now());

        assertThat(service.requiereAlertaStockBajo(producto)).isFalse();
    }

    @Test
    void requiereAlertaVencimiento_cuandoFechaDentroDelRango() {
        Producto producto = productoBase(20, 5, LocalDate.now().plusDays(10));

        assertThat(service.requiereAlertaVencimiento(producto, 30)).isTrue();
    }

    @Test
    void noRequiereAlertaVencimiento_cuandoNoHayFecha() {
        Producto producto = productoBase(20, 5, null);

        assertThat(service.requiereAlertaVencimiento(producto, 30)).isFalse();
    }

    @Test
    void crearAlertaVencimiento_incluyeDiasRestantes() {
        Producto producto = productoBase(20, 5, LocalDate.now().plusDays(3));

        var alerta = service.crearAlertaVencimientoProximo(producto);

        assertThat(alerta.mensaje()).contains("vence en 3 día(s)");
        assertThat(alerta.tipoAlerta().name()).isEqualTo("VENCIMIENTO_PROXIMO");
    }

    private Producto productoBase(int stockActual, int stockMinimo, LocalDate vencimiento) {
        return new Producto(
                1L,
                "Urea",
                "Fertilizante",
                BigDecimal.valueOf(50000),
                stockActual,
                stockMinimo,
                vencimiento,
                1L,
                "Granos",
                EstadoGeneral.ACTIVO,
                LocalDateTime.now()
        );
    }
}
