package com.example.proxyapi.integration;

import com.example.proxyapi.dto.openai.*;
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

    /**
     * Вложенный класс для тестирования POST /v1/images/generations.
     */
    @Nested
    @DisplayName("POST /v1/images/generations - Интеграционные тесты генерации изображений")
    class PostImageGenerationsIntegrationTests {

        /**
         * Общий метод для выполнения POST-запроса и проверки статуса ответа.
         *
         * @param model   Название модели.
         * @param prompt  Текстовый prompt для генерации изображения.
         * @param n       Количество изображений для генерации.
         * @param size    Размер изображения.
         * @param quality Качество изображения (опционально).
         */
        private void performGenerateImageTest(String model, String prompt, int n, String size, String quality) {
            String url = getBaseUrl() + "/images/generations";

            // Подготовка запроса
            ImageGenerationRequestDTO requestDTO = new ImageGenerationRequestDTO(
                    model,
                    prompt,
                    n,
                    size,
                    quality
            );

            // Установка заголовков
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(proxyApiKey);
            HttpEntity<ImageGenerationRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

            // Выполнение POST-запроса
            ResponseEntity<ImageGenerationResponseDTO> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    ImageGenerationResponseDTO.class
            );

            // Проверка статуса ответа
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // Проверка наличия тела ответа
            ImageGenerationResponseDTO responseBody = response.getBody();
            assertThat(responseBody).isNotNull();
            assertThat(responseBody.getData()).isNotEmpty();

            // Проверка структуры данных в ответе
            for (ImageDataDTO imageData : responseBody.getData()) {
                assertThat(imageData.getUrl()).isNotEmpty();
                // Проверка валидности URL
                assertThat(imageData.getUrl()).startsWith("http");

                if (quality != null && quality.equalsIgnoreCase("hd")) {
                    // В данном примере просто проверяем наличие base64
                    assertThat(imageData.getBase64()).isNotEmpty();
                }
            }
        }

        @Test
        @DisplayName("POST /v1/images/generations - Генерация изображений с моделью dall-e-2")
        void testGenerateMultipleImagesWithDallE2() {
            performGenerateImageTest(
                    "dall-e-2",
                    "яркий закат над океаном",
                    2,
                    "256x256",
                    null // quality не требуется для dall-e-2
            );
        }
    }

    /**
     * Вложенный класс для тестирования POST /v1/audio/speech.
     */
    @Nested
    @DisplayName("POST /v1/audio/speech - Интеграционные тесты TTS")
    class PostAudioSpeechIntegrationTests {

        /**
         * Общий метод для выполнения POST-запроса и проверки статуса ответа.
         *
         * @param model  Название модели (`tts-1` или `tts-1-hd`).
         * @param voice  Голос для генерации аудио.
         * @param input  Текст для преобразования в аудио.
         * @param format Формат выходного аудио.
         */
        private void performGenerateSpeechTest(String model, String voice, String input, String format) {
            String url = getBaseUrl() + "/audio/speech";

            // Подготовка запроса
            AudioSpeechRequestDTO requestDTO = new AudioSpeechRequestDTO(
                    model,
                    voice,
                    input,
                    format
            );

            // Установка заголовков
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(proxyApiKey);
            HttpEntity<AudioSpeechRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

            // Выполнение POST-запроса
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    url,
                    requestEntity,
                    byte[].class
            );

            // Проверка статуса ответа
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // Проверка наличия тела ответа
            byte[] audioData = response.getBody();
            assertThat(audioData).isNotNull();
            assertThat(audioData.length).isGreaterThan(0);

            // Проверка заголовков ответа
            HttpHeaders responseHeaders = response.getHeaders();
            assertThat(responseHeaders.getContentType()).isNotNull();
            assertThat(responseHeaders.getContentType().toString()).isEqualTo(getMediaTypeForFormat(format).toString());
            assertThat(responseHeaders.getContentDisposition()).isNotNull();
            assertThat(responseHeaders.getContentDisposition().getFilename()).isEqualTo("speech." + format.toLowerCase());
        }

        /**
         * Вспомогательный метод для определения типа контента по формату.
         *
         * @param format Формат аудио файла.
         * @return Соответствующий MediaType.
         */
        private MediaType getMediaTypeForFormat(String format) {
            return switch (format.toLowerCase()) {
                case "mp3" -> MediaType.parseMediaType("audio/mpeg");
                case "opus" -> MediaType.parseMediaType("audio/opus");
                case "aac" -> MediaType.parseMediaType("audio/aac");
                case "flac" -> MediaType.parseMediaType("audio/flac");
                default -> MediaType.APPLICATION_OCTET_STREAM;
            };
        }

        @Test
        @DisplayName("POST /v1/audio/speech - Успешная генерация аудио с моделью tts-1")
        void testGenerateSpeechWithTts1() {
            performGenerateSpeechTest(
                    "tts-1",
                    "alloy",
                    "Today is a wonderful day to build something people love!",
                    "mp3"
            );
        }
    }
}
