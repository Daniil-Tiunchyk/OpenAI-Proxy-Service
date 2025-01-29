package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для элемента массива "choices" в Chat Completion Response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChoiceDTO {

    @Schema(description = "Причина завершения ответа", example = "stop")
    private String finish_reason;

    @Schema(description = "Индекс выбора", example = "0")
    private int index;

    @Schema(description = "Сообщение ассистента", example = "{\"content\": \"This is a test!\", \"role\": \"assistant\"}")
    private MessageDTO message;

    @Schema(description = "Логарифмические вероятности токенов", example = "null")
    private Object logprobs;
}
