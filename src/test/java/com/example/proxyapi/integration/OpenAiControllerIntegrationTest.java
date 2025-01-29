package com.example.proxyapi.integration;

import com.example.proxyapi.dto.openai.ChatCompletionRequestInputDTO;
import com.example.proxyapi.dto.openai.ChatCompletionResponseDTO;
import com.example.proxyapi.dto.openai.MessageDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты для OpenAiController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenAiControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${proxyapi.key}")
    private String proxyApiKey;

    // Набор моделей, для которых используется temperature=1 и maxCompletionTokens
    private static final Set<String> MODELS_WITH_DEFAULT_TEMPERATURE = Set.of("o1", "o1-mini");

    /**
     * Получение базового URL приложения.
     */
    private String getBaseUrl() {
        return "http://localhost:" + port + "/openai/v1";
    }

    /**
     * Тестирование GET /v1/models.
     */
    @Test
    @DisplayName("GET /v1/models - Успешный ответ")
    void testGetModels() throws Exception {
        String url = getBaseUrl() + "/models";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(proxyApiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        // Логирование ответа
        System.out.println("Response Body: " + response.getBody());

        // Проверка статуса
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Проверка тела ответа
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Парсинг JSON-ответа
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Проверка поля "object"
        assertThat(jsonNode.get("object").asText()).isEqualTo("list");

        // Проверка, что поле "data" является массивом и не пустое
        assertThat(jsonNode.get("data").isArray()).isTrue();
        assertThat(jsonNode.get("data").size()).isGreaterThan(0);
    }

    /**
     * Вложенный класс для тестирования POST /v1/chat/completions с различными моделями.
     */
    @Nested
    @DisplayName("POST /v1/chat/completions - Интеграционные тесты различных моделей")
    class PostChatCompletionsIntegrationTests {

        /**
         * Общий метод для выполнения POST-запроса и проверки статуса ответа.
         *
         * @param model Название модели.
         */
        private void performPostTest(String model) {
            String url = getBaseUrl() + "/chat/completions";

            // Определение, требует ли модель temperature=1 и maxCompletionTokens
            boolean requiresDefaultTemperature = MODELS_WITH_DEFAULT_TEMPERATURE.contains(model);

            // Установка temperature в зависимости от модели
            Double temperature = requiresDefaultTemperature ? 1.0 : 0.7;

            // Подготовка запроса
            ChatCompletionRequestInputDTO requestInputDTO = new ChatCompletionRequestInputDTO(
                    model,
                    List.of(
                            new MessageDTO("user", "Say this is a test!")
                    ),
                    100, // maxTokens
                    temperature // temperature
            );

            // Установка заголовков
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(proxyApiKey);
            HttpEntity<ChatCompletionRequestInputDTO> requestEntity = new HttpEntity<>(requestInputDTO, headers);

            // Выполнение POST-запроса
            ResponseEntity<ChatCompletionResponseDTO> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    ChatCompletionResponseDTO.class
            );

            // Проверка статуса ответа
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // Проверка наличия тела ответа
            assertThat(response.getBody()).isNotNull();
        }

        // Создаём отдельные тесты для каждой модели
        @Test
        @DisplayName("POST /v1/chat/completions - Модель o1")
        void testCreateChatCompletionWithO1() {
            performPostTest("o1");
        }

        @Test
        @DisplayName("POST /v1/chat/completions - Модель o1-mini")
        void testCreateChatCompletionWithO1Mini() {
            performPostTest("o1-mini");
        }

        @Test
        @DisplayName("POST /v1/chat/completions - Модель gpt-4o")
        void testCreateChatCompletionWithGpt4o() {
            performPostTest("gpt-4o");
        }

        @Test
        @DisplayName("POST /v1/chat/completions - Модель gpt-4o-mini")
        void testCreateChatCompletionWithGpt4oMini() {
            performPostTest("gpt-4o-mini");
        }

        @Test
        @DisplayName("POST /v1/chat/completions - Модель gpt-4-turbo")
        void testCreateChatCompletionWithGpt4Turbo() {
            performPostTest("gpt-4-turbo");
        }

        @Test
        @DisplayName("POST /v1/chat/completions - Модель gpt-4")
        void testCreateChatCompletionWithGpt4() {
            performPostTest("gpt-4");
        }

        @Test
        @DisplayName("POST /v1/chat/completions - Модель gpt-3.5-turbo")
        void testCreateChatCompletionWithGpt35Turbo() {
            performPostTest("gpt-3.5-turbo");
        }
    }
}
