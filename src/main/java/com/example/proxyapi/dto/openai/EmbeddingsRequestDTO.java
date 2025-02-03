package com.example.proxyapi.dto.openai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmbeddingsRequestDTO {

    /**
     * Название модели для получения Embeddings.
     * Например: "text-embedding-3-small", "text-embedding-3-large", "text-embedding-ada-002" и т.д.
     */
    @NotBlank
    @Schema(description = "Название модели, например 'text-embedding-3-small'.", example = "text-embedding-3-small")
    private String model;

    /**
     * Текст (или массив текстов) для которого получаем embedding.
     * Для упрощения — одна строка.
     */
    @NotBlank
    @Schema(description = "Текст, для которого нужно получить Embeddings.", example = "Ваш текст здесь")
    private String input;
}
