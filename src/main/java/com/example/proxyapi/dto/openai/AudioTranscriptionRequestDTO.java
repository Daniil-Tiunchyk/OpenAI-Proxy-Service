package com.example.proxyapi.dto.openai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AudioTranscriptionRequestDTO {

    /**
     * Название модели, например, "whisper-1".
     */
    @NotBlank
    @Pattern(regexp = "whisper-1", message = "Model must be 'whisper-1'")
    @Schema(description = "Название модели, например, 'whisper-1'.", example = "whisper-1")
    private String model;

    /**
     * Формат ответа. Возможные значения: "json" (по умолчанию), "text".
     */
    @Schema(description = "Формат ответа. Возможные значения: 'json' (по умолчанию), 'text'.", example = "json")
    private String response_format;

    /**
     * Дополнительный prompt для улучшения качества транскрипции.
     */
    @Schema(description = "Дополнительный prompt для улучшения качества транскрипции.", example = "Используй правильную пунктуацию и заглавные буквы.")
    private String prompt;
}
