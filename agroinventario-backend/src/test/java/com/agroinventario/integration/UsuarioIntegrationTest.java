package com.agroinventario.integration;

import com.agroinventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioIntegrationTest extends AbstractIntegrationTest {

    @Test
    void listarUsuarios_comoAdmin_devuelve200() throws Exception {
        mockMvc.perform(get("/v1/usuarios")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void listarUsuarios_comoEmpleado_devuelve403() throws Exception {
        mockMvc.perform(get("/v1/usuarios")
                        .header("Authorization", "Bearer " + empleadoToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearUsuario_comoAdmin_devuelve201() throws Exception {
        mockMvc.perform(post("/v1/usuarios")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Nuevo Usuario",
                                "email", "nuevo.usuario@test.local",
                                "password", "Password123!",
                                "roles", Set.of("EMPLEADO")
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("nuevo.usuario@test.local"))
                .andExpect(jsonPath("$.data.roles[0]").value("EMPLEADO"));
    }
}
