package com.example.proxyapi.dto.openai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioSpeechRequestDTO {

    /**
     * Название модели, например, "tts-1" или "tts-1-hd".
     */
    @NotNull
    @Pattern(regexp = "tts-1|tts-1-hd", message = "Model must be either 'tts-1' or 'tts-1-hd'")
    @Schema(description = "Название модели, например, 'tts-1' или 'tts-1-hd'.", example = "tts-1-hd")
    private String model;

    /**
     * Голос для генерации аудио.
     */
    @NotBlank
    @Schema(description = "Голос для генерации аудио. Доступные варианты: alloy, echo, fable, onyx, nova, shimmer.", example = "alloy")
    private String voice;

    /**
     * Текст для преобразования в аудио.
     */
    @NotBlank
    @Schema(description = "Текст для преобразования в аудио.", example = "Today is a wonderful day to build something people love!")
    private String input;

    /**
     * Формат выходного аудио. По умолчанию "mp3".
     */
    @Schema(description = "Формат выходного аудио. Доступные варианты: mp3, opus, aac, flac.", example = "mp3")
    private String format;
}
