package com.agroinventario.application.service.ia;

import com.agroinventario.application.service.ia.ClasificadorIntencionInventario.Intencion;
import com.agroinventario.domain.model.EstadoGeneral;
import com.agroinventario.domain.model.Producto;
import com.agroinventario.domain.model.ProyeccionInventario;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConversacionInventarioTest {

    private final Producto urea = new Producto(
            1L, "Urea 46%", "fertilizante", BigDecimal.TEN, 40, 10,
            LocalDate.now().plusDays(120), 1L, "Alimentos", EstadoGeneral.ACTIVO, LocalDateTime.now());

    @Test
    void saludoNoMencionaClavesNiMotores() {
        var resultado = ClasificadorIntencionInventario.clasificar("hola", List.of(urea));
        assertThat(resultado.principal()).isEqualTo(Intencion.SALUDO);

        String respuesta = GeneradorConversacionInventario.responder(
                resultado, List.of(urea), List.of(), List.of(), 150_016);

        assertThat(respuesta.toLowerCase()).contains("hola");
        assertThat(respuesta).doesNotContain("IA_API_KEY", "LLM", "REGLAS", "API");
    }

    @Test
    void inventarioGeneralHablaEnLenguajeNatural() {
        var resultado = ClasificadorIntencionInventario.clasificar("¿Cómo está el inventario ahora?", List.of(urea));
        assertThat(resultado.principal()).isEqualTo(Intencion.RESUMEN);

        String respuesta = GeneradorConversacionInventario.responder(
                resultado, List.of(urea), List.of(), List.of(proyeccion()), 150_016);

        assertThat(respuesta).contains("inventario");
        assertThat(respuesta).contains("registros de consumo");
        assertThat(respuesta).doesNotContain("IA_API_KEY", "media móvil", "Configure");
    }

    @Test
    void pedidoSeEntiendeComoReposicion() {
        var resultado = ClasificadorIntencionInventario.clasificar("¿Qué debo pedir para no desabastecerme?", List.of(urea));
        assertThat(resultado.principal()).isEqualTo(Intencion.REPOSICION);

        String respuesta = GeneradorConversacionInventario.responder(
                resultado, List.of(urea), List.of(), List.of(proyeccion()), 150_016);

        assertThat(respuesta.toLowerCase()).contains("pedir");
        assertThat(respuesta).contains("Urea 46%");
    }

    @Test
    void productoPorNombre() {
        var resultado = ClasificadorIntencionInventario.clasificar("¿Cuánto queda de Urea 46%?", List.of(urea));
        assertThat(resultado.principal()).isEqualTo(Intencion.PRODUCTO);

        String respuesta = GeneradorConversacionInventario.responder(
                resultado, List.of(urea), List.of(), List.of(proyeccion()), 150_016);

        assertThat(respuesta).contains("Urea 46%");
        assertThat(respuesta).contains("40");
        assertThat(respuesta.toLowerCase()).contains("pedir");
    }

    @Test
    void graciasEsSocial() {
        var resultado = ClasificadorIntencionInventario.clasificar("gracias", List.of(urea));
        assertThat(resultado.esSocial()).isTrue();
        assertThat(resultado.principal()).isEqualTo(Intencion.GRACIAS);
    }

    private ProyeccionInventario proyeccion() {
        return new ProyeccionInventario(
                1L, "Urea 46%", "Alimentos", 40, 10, 2.0, 0.1, 0.4, 7,
                20, LocalDate.now().plusDays(20), 14, 60, 18,
                "TOP_OFF_LEAD_TIME", "DEMANDA_LEAD_TIME", "MEDIO",
                "Con el consumo proyectado se agota en 20 días.", 0.82, "HISTORICO_REAL", 90, 120);
    }
}
