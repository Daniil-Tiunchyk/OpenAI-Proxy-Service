package com.example.proxyapi.dto.openai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AudioResponseDTO {

    /**
     * Транскрибированный или переведённый текст.
     */
    @Schema(description = "Транскрибированный или переведённый текст.", example = "Привет, меня зовут Вольфганг и я из Германии.")
    private String text;
}
