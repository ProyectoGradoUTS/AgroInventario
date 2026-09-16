package com.agroinventario.application.service.ia;

import com.agroinventario.infrastructure.config.IaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class LlmInventarioClient {

    private static final Logger log = LoggerFactory.getLogger(LlmInventarioClient.class);
    private static final String SISTEMA = """
            Eres un compañero de trabajo que ayuda con el inventario agropecuario de Agro Inventario.
            Habla en español natural, claro y cercano, de usted, como en una conversación.
            Usa solo las cifras del contexto. No inventes productos ni cantidades.
            No menciones APIs, claves, LLM, reglas, media móvil, confianza porcentual ni nombres técnicos internos.
            Si aplica, diga qué se agota, qué vence y qué conviene pedir, con cantidades concretas.
            """;

    private final IaProperties iaProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public LlmInventarioClient(IaProperties iaProperties, ObjectMapper objectMapper) {
        this.iaProperties = iaProperties;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int timeout = iaProperties.timeoutMs() <= 0 ? 8000 : iaProperties.timeoutMs();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public Optional<String> responder(String pregunta, String contexto) {
        if (!iaProperties.llmHabilitado()) {
            return Optional.empty();
        }
        try {
            String provider = iaProperties.provider().trim().toLowerCase();
            if ("openai".equals(provider)) {
                return Optional.ofNullable(llamarOpenAi(pregunta, contexto));
            }
            if ("gemini".equals(provider)) {
                return Optional.ofNullable(llamarGemini(pregunta, contexto));
            }
        } catch (Exception ex) {
            log.warn("LLM no disponible, se usará el motor de inventario: {}", ex.getMessage());
        }
        return Optional.empty();
    }

    private String llamarOpenAi(String pregunta, String contexto) {
        String model = iaProperties.model() == null || iaProperties.model().isBlank()
                ? "gpt-4o-mini"
                : iaProperties.model();
        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.2,
                "max_tokens", 500,
                "messages", List.of(
                        Map.of("role", "system", "content", SISTEMA),
                        Map.of("role", "user", "content", contexto + "\n\nPregunta del usuario:\n" + pregunta)
                )
        );
        String raw = restClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + iaProperties.apiKey())
                .body(body)
                .retrieve()
                .body(String.class);
        return leerRuta(raw, "/choices/0/message/content");
    }

    private String llamarGemini(String pregunta, String contexto) {
        String model = iaProperties.model() == null || iaProperties.model().isBlank()
                ? "gemini-2.0-flash"
                : iaProperties.model();
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", SISTEMA + "\n\n" + contexto + "\n\nPregunta:\n" + pregunta))
                )),
                "generationConfig", Map.of("temperature", 0.2, "maxOutputTokens", 500)
        );
        String raw = restClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}",
                        model, iaProperties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        return leerRuta(raw, "/candidates/0/content/parts/0/text");
    }

    private String leerRuta(String raw, String pointer) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(raw).at(pointer);
            if (node == null || node.isMissingNode() || node.asText().isBlank()) {
                return null;
            }
            return node.asText().trim();
        } catch (Exception ex) {
            log.debug("No se pudo parsear respuesta LLM: {}", ex.getMessage());
            return null;
        }
    }
}
