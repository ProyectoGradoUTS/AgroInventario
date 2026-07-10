package com.agroinventario.integration;

import com.agroinventario.support.AbstractIntegrationTest;
import com.agroinventario.support.IntegrationTestDataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void login_conCredencialesValidas_devuelveToken() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", IntegrationTestDataConfig.ADMIN_EMAIL,
                                "password", IntegrationTestDataConfig.ADMIN_PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"));
    }

    @Test
    void login_conPasswordIncorrecta_devuelve401() throws Exception {
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", IntegrationTestDataConfig.ADMIN_EMAIL,
                                "password", "incorrecta"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_conTokenValido_devuelveUsuario() throws Exception {
        String token = adminToken();

        mockMvc.perform(get("/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(IntegrationTestDataConfig.ADMIN_EMAIL));
    }

    @Test
    void me_sinToken_devuelve401() throws Exception {
        mockMvc.perform(get("/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
