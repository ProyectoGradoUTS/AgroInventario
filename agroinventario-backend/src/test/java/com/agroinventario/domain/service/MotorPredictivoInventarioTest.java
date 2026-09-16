package com.agroinventario.domain.service;

import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import com.agroinventario.domain.model.SerieConsumoDiario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MotorPredictivoInventarioTest {

    private final MotorPredictivoInventario motor = new MotorPredictivoInventario();

    @Test
    void proyectaAgotamientoConSerieHistorica() {
        Producto producto = new Producto(
                1L, "Urea 46%", "fertilizante", BigDecimal.TEN, 8, 10,
                LocalDate.now().plusDays(120), 1L, "Alimentos", EstadoGeneral.ACTIVO, LocalDateTime.now());

        List<SerieConsumoDiario> serie = new ArrayList<>();
        for (int i = 90; i >= 1; i--) {
            serie.add(new SerieConsumoDiario(LocalDate.now().minusDays(i), 0, 2, 20 + i));
        }

        ProyeccionInventario proyeccion = motor.proyectar(producto, serie, "HISTORICO_REAL");

        assertThat(proyeccion.consumoPromedio()).isEqualTo(2.0);
        assertThat(proyeccion.diasHastaAgotamiento()).isEqualTo(4);
        assertThat(proyeccion.fuenteDatos()).isEqualTo("HISTORICO_REAL");
        assertThat(proyeccion.nivelConfianza()).isGreaterThan(0.5);
        assertThat(proyeccion.cantidadSugerida()).isGreaterThan(0);
    }

    @Test
    void marcaCriticoCuandoNoHayStock() {
        Producto producto = new Producto(
                2L, "Ivermectina", "salud", BigDecimal.ONE, 0, 5,
                LocalDate.now().plusDays(10), 1L, "Medicina", EstadoGeneral.ACTIVO, LocalDateTime.now());

        ProyeccionInventario proyeccion = motor.proyectar(producto, List.of(), "SINTETICO");

        assertThat(proyeccion.diasHastaAgotamiento()).isZero();
        assertThat(proyeccion.riesgo()).isEqualTo("CRITICO");
        assertThat(proyeccion.cantidadSugerida()).isGreaterThan(0);
    }
}
