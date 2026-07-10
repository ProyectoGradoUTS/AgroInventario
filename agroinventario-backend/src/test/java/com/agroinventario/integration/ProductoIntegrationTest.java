package com.agroinventario.integration;

import com.agroinventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductoIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;
    private Long categoriaId;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = adminToken();

        MvcResult categoriaResult = mockMvc.perform(post("/v1/categorias")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Test Grano",
                                "descripcion", "Categoría de prueba"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        categoriaId = objectMapper.readTree(categoriaResult.getResponse().getContentAsString())
                .path("data")
                .path("id")
                .asLong();
    }

    @Test
    void crearProducto_comoAdmin_devuelve201() throws Exception {
        mockMvc.perform(post("/v1/productos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Urea 46%",
                                "descripcion", "Fertilizante",
                                "precio", 150000,
                                "stockActual", 20,
                                "stockMinimo", 5,
                                "categoriaId", categoriaId,
                                "estado", "ACTIVO"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.nombre").value("Urea 46%"))
                .andExpect(jsonPath("$.data.stockActual").value(20));
    }

    @Test
    void listarProductos_comoEmpleado_devuelve200() throws Exception {
        String empleadoToken = empleadoToken();

        mockMvc.perform(get("/v1/productos")
                        .header("Authorization", "Bearer " + empleadoToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void crearProducto_comoEmpleado_devuelve403() throws Exception {
        String empleadoToken = empleadoToken();

        mockMvc.perform(post("/v1/productos")
                        .header("Authorization", "Bearer " + empleadoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "No permitido",
                                "descripcion", "x",
                                "precio", 1000,
                                "stockActual", 1,
                                "stockMinimo", 1,
                                "categoriaId", categoriaId,
                                "estado", "ACTIVO"
                        ))))
                .andExpect(status().isForbidden());
    }
}
