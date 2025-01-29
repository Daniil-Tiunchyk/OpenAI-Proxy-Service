package com.example.proxyapi.controller;

import com.example.proxyapi.dto.openai.ChatCompletionRequestInputDTO;
import com.example.proxyapi.dto.openai.ChatCompletionResponseDTO;
import com.example.proxyapi.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с OpenAI (через ProxyAPI).
 * <p>
 * Имеет эндпоинты:
 * 1. GET /v1/models
 * 2. POST /v1/chat/completions
 */
@Tag(name = "OpenAI (ProxyAPI)", description = "Эндпоинты для взаимодействия с OpenAI через ProxyAPI")
@RestController
@RequestMapping("/openai/v1")
public class OpenAiController {

    private static final Logger log = LoggerFactory.getLogger(OpenAiController.class);

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
}
