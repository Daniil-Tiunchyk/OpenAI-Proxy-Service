package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * DTO для внутреннего использования при запросе к OpenAI /v1/chat/completions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequestDTO {

    /**
     * Название модели, например, "gpt-4-turbo".
     */
    @NotNull
    @Schema(description = "Название модели, например, 'gpt-4-turbo'.", example = "gpt-3.5-turbo")
    private String model;

    /**
     * Список сообщений в диалоге.
     */
    @NotEmpty
    @Schema(description = "Список сообщений в диалоге.", example = "[{\"role\": \"user\", \"content\": \"Say this is a test!\"}]")
    private List<MessageDTO> messages;

    /**
     * Максимальное количество токенов для генерации.
     */
    @JsonProperty("max_tokens")
    @Schema(description = "Максимальное количество токенов для генерации.", example = "100")
    private Integer maxTokens;

    /**
     * Максимальное количество токенов для генерации (специфично для некоторых моделей).
     */
    @JsonProperty("max_completion_tokens")
    @Schema(description = "Максимальное количество токенов для генерации (специфично для некоторых моделей).", example = "100")
    private Integer maxCompletionTokens;

    /**
     * Параметр "temperature" контролирует степень случайности.
     */
    @Schema(description = "Параметр 'temperature' контролирует степень случайности.", example = "0.7")
    private Double temperature;
}
