package com.agroinventario.integration;

import com.agroinventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventarioIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;
    private Long productoId;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = adminToken();

        MvcResult categoriaResult = mockMvc.perform(post("/v1/categorias")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Inventario Cat",
                                "descripcion", "Para movimientos"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        Long categoriaId = objectMapper.readTree(categoriaResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        MvcResult productoResult = mockMvc.perform(post("/v1/productos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Producto Inventario",
                                "descripcion", "Stock inicial",
                                "precio", 50000,
                                "stockActual", 10,
                                "stockMinimo", 2,
                                "categoriaId", categoriaId,
                                "estado", "ACTIVO"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        productoId = objectMapper.readTree(productoResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    void registrarEntrada_actualizaStock() throws Exception {
        mockMvc.perform(post("/v1/inventario/productos/{id}/movimientos", productoId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tipoMovimiento", "ENTRADA",
                                "cantidad", 5,
                                "descripcion", "Reposición"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.cantidad").value(5))
                .andExpect(jsonPath("$.data.tipoMovimiento").value("ENTRADA"));
    }

    @Test
    void registrarSalida_sinStockSuficiente_devuelve400() throws Exception {
        mockMvc.perform(post("/v1/inventario/productos/{id}/movimientos", productoId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tipoMovimiento", "SALIDA",
                                "cantidad", 999,
                                "descripcion", "Salida excesiva"
                        ))))
                .andExpect(status().isBadRequest());
    }
}
