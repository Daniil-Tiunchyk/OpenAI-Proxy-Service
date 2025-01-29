package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO для запроса генерации изображения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageGenerationRequestDTO {

    /**
     * Название модели, например, "dall-e-3" или "dall-e-2".
     */
    @NotNull
    @Schema(description = "Название модели, например, 'dall-e-3' или 'dall-e-2'.", example = "dall-e-2")
    private String model;

    /**
     * Текстовый prompt для генерации изображения.
     */
    @NotEmpty
    @Schema(description = "Текстовый prompt для генерации изображения.", example = "белый сиамский кот")
    private String prompt;

    /**
     * Количество изображений для генерации.
     */
    @Schema(description = "Количество изображений для генерации.", example = "1")
    private Integer n;

    /**
     * Размер изображения, например, "256x256".
     */
    @Schema(description = "Размер изображения, например, '256x256'.", example = "256x256")
    private String size;

    /**
     * Качество изображения, например "hd".
     */
    @Schema(description = "Качество изображения.", example = "hd")
    private String quality;
}

