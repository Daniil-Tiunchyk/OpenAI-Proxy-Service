package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * DTO для ответа от OpenAI /v1/chat/completions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponseDTO {

    @Schema(description = "Список вариантов ответа", example = "[{\"finish_reason\": \"stop\", \"index\": 0, \"message\": {\"content\": \"This is a test!\", \"role\": \"assistant\"}, \"logprobs\": null}]")
    private List<ChoiceDTO> choices;

    @Schema(description = "Время создания ответа (Unix timestamp)", example = "1677664795")
    private long created;

    @Schema(description = "Уникальный идентификатор ответа", example = "chatcmpl-7QyqpwdfhqwajicIEznoc6Q47XAyW")
    private String id;

    @Schema(description = "Используемая модель", example = "gpt-3.5-turbo-0613")
    private String model;

    @Schema(description = "Тип объекта", example = "chat.completion")
    private String object;

    @Schema(description = "Информация о количестве использованных токенов", example = "{\"completion_tokens\":17,\"prompt_tokens\":57,\"total_tokens\":74}")
    private UsageDTO usage;

    /**
     * DTO для элемента массива "choices" в Chat Completion Response.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChoiceDTO {

        @Schema(description = "Причина завершения ответа", example = "stop")
        private String finish_reason;

        @Schema(description = "Индекс выбора", example = "0")
        private int index;

        @Schema(description = "Сообщение ассистента", example = "{\"content\": \"This is a test!\", \"role\": \"assistant\"}")
        private MessageDTO message;

        @Schema(description = "Логарифмические вероятности токенов", example = "null")
        private Object logprobs;
    }

    /**
     * DTO для поля "usage" в Chat Completion Response.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageDTO {

        @Schema(description = "Количество токенов, использованных для промпта", example = "57")
        private int prompt_tokens;

        @Schema(description = "Количество токенов, использованных для завершения", example = "17")
        private int completion_tokens;

        @Schema(description = "Общее количество использованных токенов", example = "74")
        private int total_tokens;
    }
}
