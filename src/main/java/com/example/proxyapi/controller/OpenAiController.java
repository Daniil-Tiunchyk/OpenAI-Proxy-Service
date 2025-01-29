package com.example.proxyapi.controller;

import com.example.proxyapi.dto.openai.*;
import com.example.proxyapi.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с OpenAI (через ProxyAPI).
 * <p>
 * Имеет эндпоинты:
 * 1. GET /v1/models
 * 2. POST /v1/chat/completions
 * 3. POST /v1/images/generations
 */
@Tag(name = "OpenAI (ProxyAPI)", description = "Эндпоинты для взаимодействия с OpenAI через ProxyAPI")
@RestController
@RequestMapping("/openai/v1")
@Slf4j
@Validated
public class OpenAiController {

    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    /**
     * Получить список доступных моделей.
     *
     * @return JSON-строка со списком моделей
     */
    @Operation(
            summary = "Получить список моделей",
            description = "Метод позволяет узнать, какие модели доступны в OpenAI (через ProxyAPI)."
    )
    @GetMapping("/models")
    public ResponseEntity<String> listModels() {
        log.info("GET /openai/v1/models - запрошен список моделей");
        String modelsJson = openAiService.listModels();
        return ResponseEntity.ok(modelsJson);
    }

    /**
     * Создать Chat Completion.
     *
     * @param requestInputDTO Запрос с параметрами
     * @return Ответ от OpenAI как ChatCompletionResponseDTO
     */
    @Operation(
            summary = "Создать чат-комплишен",
            description = """
                    Отправляет запрос на /v1/chat/completions в OpenAI (через ProxyAPI).
                    """
    )
    @PostMapping("/chat/completions")
    public ResponseEntity<ChatCompletionResponseDTO> createChatCompletion(
            @Valid @RequestBody ChatCompletionRequestInputDTO requestInputDTO
    ) {
        log.info("POST /openai/v1/chat/completions - входящие данные: {}", requestInputDTO);
        ChatCompletionResponseDTO response = openAiService.createChatCompletion(requestInputDTO);
        log.info("POST /openai/v1/chat/completions - ответ: {}", response);
        return ResponseEntity.ok(response);
    }

    /**
     * Генерация изображений на основе текстового prompt.
     *
     * @param requestDTO Запрос с параметрами генерации
     * @return Ответ от OpenAI как ImageGenerationResponseDTO
     */
    @Operation(
            summary = "Генерация изображений",
            description = "Создает оригинальное изображение на основе текстового prompt с использованием DALL·E 2 или DALL·E 3."
    )
    @PostMapping("/images/generations")
    public ResponseEntity<ImageGenerationResponseDTO> generateImage(
            @Valid @RequestBody ImageGenerationRequestDTO requestDTO
    ) {
        log.info("POST /openai/v1/images/generations - входящие данные: {}", requestDTO);
        ImageGenerationResponseDTO response = openAiService.generateImage(requestDTO);
        log.info("POST /openai/v1/images/generations - ответ: {}", response);
        return ResponseEntity.ok(response);
    }
}
