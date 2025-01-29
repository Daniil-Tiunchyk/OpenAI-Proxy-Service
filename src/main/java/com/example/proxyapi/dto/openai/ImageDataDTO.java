package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO для представления данных изображения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageDataDTO {

    /**
     * URL сгенерированного изображения.
     */
    @Schema(description = "URL сгенерированного изображения.", example = "https://example.com/image.png")
    private String url;

    /**
     * Изображение в формате Base64, если запрошено.
     */
    @Schema(description = "Изображение в формате Base64.")
    private String base64;
}
