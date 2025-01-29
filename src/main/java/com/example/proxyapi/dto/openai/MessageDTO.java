package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO для сообщений в Chat Completion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {

    /**
     * Роль отправителя: "system", "user" или "assistant".
     */
    @NotNull
    @Schema(description = "Роль отправителя: 'system', 'user' или 'assistant'.", example = "user")
    private String role;

    /**
     * Содержимое сообщения.
     */
    @NotNull
    @Schema(description = "Содержимое сообщения.", example = "Hello!")
    private String content;
}
