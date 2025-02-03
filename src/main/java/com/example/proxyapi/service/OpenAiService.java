package com.example.proxyapi.service;

import com.example.proxyapi.dto.openai.*;
import com.example.proxyapi.exception.ProxyApiException;
import com.example.proxyapi.utils.FilePart;
import com.example.proxyapi.utils.ProxyApiHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Сервис для взаимодействия с OpenAI через ProxyAPI.
 */
@Service
public class OpenAiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${proxyapi.key}")
    private String proxyApiKey;

    private static final String BASE_URL = "https://api.proxyapi.ru/openai";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Набор моделей, для которых используется max_completion_tokens и temperature=1
    private static final Set<String> MODELS_WITH_MAX_COMPLETION_TOKENS = Set.of("o1", "o1-mini");

    /**
     * Получить список доступных моделей.
     *
     * @return JSON-строка со списком моделей
     */
    public String listModels() {
        String url = BASE_URL + "/v1/models";
        try {
            return ProxyApiHttpClient.sendGet(url, proxyApiKey);
        } catch (IOException | InterruptedException e) {
            throw new ProxyApiException("Ошибка при получении списка моделей (OpenAI)", e);
        }
    }

    /**
     * Создать Chat Completion.
     *
     * @param requestInputDTO Запрос с параметрами
     * @return Ответ от OpenAI как ChatCompletionResponseDTO
     */
    public ChatCompletionResponseDTO createChatCompletion(ChatCompletionRequestInputDTO requestInputDTO) {
        String url = BASE_URL + "/v1/chat/completions";
        try {
            // Маппинг входящего DTO на внутренний DTO
            ChatCompletionRequestDTO requestDTO = new ChatCompletionRequestDTO();
            requestDTO.setModel(requestInputDTO.getModel());
            requestDTO.setMessages(requestInputDTO.getMessages());
            requestDTO.setMaxTokens(requestInputDTO.getMaxTokens());
            requestDTO.setTemperature(requestInputDTO.getTemperature());

            // Проверка модели и настройка параметров
            if (MODELS_WITH_MAX_COMPLETION_TOKENS.contains(requestInputDTO.getModel())) {
                // Используем max_completion_tokens вместо max_tokens
                requestDTO.setMaxCompletionTokens(requestInputDTO.getMaxTokens());
                requestDTO.setMaxTokens(null);
                // Устанавливаем temperature = 1
                requestDTO.setTemperature(1.0);
                log.debug("Model {} requires max_completion_tokens and temperature=1. Setting accordingly.", requestDTO.getModel());
            }

            // Сериализация запроса в JSON
            String jsonRequest = objectMapper.writeValueAsString(requestDTO);
            log.debug("Sending POST request to {} with body: {}", url, jsonRequest);

            // Отправка POST-запроса
            String jsonResponse = ProxyApiHttpClient.sendPost(url, jsonRequest, proxyApiKey);
            log.debug("Received response: {}", jsonResponse);

            // Десериализация ответа в DTO
            return objectMapper.readValue(jsonResponse, ChatCompletionResponseDTO.class);

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при создании chat-completion (OpenAI): {}", e.getMessage(), e);
            throw new ProxyApiException("Ошибка при создании chat-completion (OpenAI)", e);
        }
    }

    /**
     * Получить Embeddings для переданного текста.
     *
     * @param requestDTO объект, содержащий model и input
     * @return EmbeddingsResponseDTO с векторным представлением текста
     */
    public EmbeddingsResponseDTO createEmbeddings(EmbeddingsRequestDTO requestDTO) {
        String url = BASE_URL + "/v1/embeddings";

        try {
            // Сериализация тела запроса в JSON
            String jsonRequest = objectMapper.writeValueAsString(requestDTO);
            log.debug("Sending POST request to {} with body: {}", url, jsonRequest);

            // Выполняем POST-запрос
            String jsonResponse = ProxyApiHttpClient.sendPost(url, jsonRequest, proxyApiKey);
            log.debug("Received embeddings response: {}", jsonResponse);

            // Десериализуем ответ
            return objectMapper.readValue(jsonResponse, EmbeddingsResponseDTO.class);

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при получении embeddings (OpenAI): {}", e.getMessage(), e);
            throw new ProxyApiException("Ошибка при получении embeddings (OpenAI)", e);
        }
    }

    /**
     * Сгенерировать изображение на основе текста.
     *
     * @param requestDTO Запрос с параметрами генерации
     * @return Ответ от OpenAI как ImageGenerationResponseDTO
     */
    public ImageGenerationResponseDTO generateImage(ImageGenerationRequestDTO requestDTO) {
        String url = BASE_URL + "/v1/images/generations";
        try {
            // Сериализация запроса в JSON
            String jsonRequest = objectMapper.writeValueAsString(requestDTO);
            log.debug("Sending POST request to {} with body: {}", url, jsonRequest);

            // Отправка POST-запроса
            String jsonResponse = ProxyApiHttpClient.sendPost(url, jsonRequest, proxyApiKey);
            log.debug("Received response: {}", jsonResponse);

            // Десериализация ответа в DTO
            return objectMapper.readValue(jsonResponse, ImageGenerationResponseDTO.class);

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при генерации изображения (OpenAI): {}", e.getMessage(), e);
            throw new ProxyApiException("Ошибка при генерации изображения (OpenAI)", e);
        }
    }

    /**
     * Сгенерировать аудио на основе текста.
     *
     * @param requestDTO Запрос с параметрами генерации аудио
     * @return Бинарные данные аудио файла
     */
    public byte[] generateSpeech(AudioSpeechRequestDTO requestDTO) {
        String url = BASE_URL + "/v1/audio/speech";
        try {
            // Сериализация запроса в JSON
            String jsonRequest = objectMapper.writeValueAsString(requestDTO);
            log.debug("Sending POST request to {} with body: {}", url, jsonRequest);

            // Отправка POST-запроса и получение бинарного ответа
            byte[] audioData = ProxyApiHttpClient.sendPostForBytes(url, jsonRequest, proxyApiKey);
            log.debug("Received audio data of length: {}", audioData.length);

            return audioData;

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при генерации аудио (OpenAI): {}", e.getMessage(), e);
            throw new ProxyApiException("Ошибка при генерации аудио (OpenAI)", e);
        }
    }

    /**
     * Транскрибировать аудио файл.
     *
     * @param file       Аудио файл
     * @param requestDTO Параметры запроса
     * @return Ответ от OpenAI как AudioResponseDTO
     */
    public AudioResponseDTO transcribeAudio(MultipartFile file, AudioTranscriptionRequestDTO requestDTO) {
        String url = BASE_URL + "/v1/audio/transcriptions";
        try {
            Map<String, String> fields = new HashMap<>();
            fields.put("model", requestDTO.getModel());

            if (requestDTO.getResponse_format() != null && !requestDTO.getResponse_format().isEmpty()) {
                fields.put("response_format", requestDTO.getResponse_format());
            }

            if (requestDTO.getPrompt() != null && !requestDTO.getPrompt().isEmpty()) {
                fields.put("prompt", requestDTO.getPrompt());
            }

            // Создание списка файлов с указанием Content-Type
            List<FilePart> fileParts = new ArrayList<>();
            file.getContentType();
            fileParts.add(new FilePart(
                    "file",
                    file.getOriginalFilename(),
                    file.getBytes(),
                    file.getContentType()
            ));

            String jsonResponse = ProxyApiHttpClient.sendMultipartPost(url, fields, fileParts, proxyApiKey);
            log.debug("Received transcription response: {}", jsonResponse);

            return objectMapper.readValue(jsonResponse, AudioResponseDTO.class);

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при транскрипции аудио (OpenAI): {}", e.getMessage(), e);
            throw new ProxyApiException("Ошибка при транскрипции аудио (OpenAI)", e);
        }
    }

    /**
     * Перевести аудио файл на английский.
     *
     * @param file       Аудио файл
     * @param requestDTO Параметры перевода
     * @return Ответ от OpenAI как AudioResponseDTO
     */
    public AudioResponseDTO translateAudio(MultipartFile file, AudioTranslationRequestDTO requestDTO) {
        String url = BASE_URL + "/v1/audio/translations";
        try {
            Map<String, String> fields = new HashMap<>();
            fields.put("model", requestDTO.getModel());

            if (requestDTO.getResponse_format() != null && !requestDTO.getResponse_format().isEmpty()) {
                fields.put("response_format", requestDTO.getResponse_format());
            }

            if (requestDTO.getPrompt() != null && !requestDTO.getPrompt().isEmpty()) {
                fields.put("prompt", requestDTO.getPrompt());
            }

            // Создание списка файлов с указанием Content-Type
            List<FilePart> fileParts = new ArrayList<>();
            file.getContentType();
            fileParts.add(new FilePart(
                    "file",
                    file.getOriginalFilename(),
                    file.getBytes(),
                    file.getContentType()
            ));

            String jsonResponse = ProxyApiHttpClient.sendMultipartPost(url, fields, fileParts, proxyApiKey);
            log.debug("Received translation response: {}", jsonResponse);

            return objectMapper.readValue(jsonResponse, AudioResponseDTO.class);

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при переводе аудио (OpenAI): {}", e.getMessage(), e);
            throw new ProxyApiException("Ошибка при переводе аудио (OpenAI)", e);
        }
    }
}
