package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class EmbeddingsResponseDTO {

    @Schema(description = "Тип объекта, обычно 'list'.", example = "list")
    private String object;

    @Schema(description = "Название модели, которая была использована.", example = "text-embedding-3-small")
    private String model;

    @Schema(description = "Список embedding-объектов.")
    private List<EmbeddingData> data;

    @Schema(description = "Информация об используемых токенах.")
    private Usage usage;

    @Data
    public static class EmbeddingData {
        @Schema(description = "Тип объекта, обычно 'embedding'.", example = "embedding")
        private String object;

        @Schema(description = "Индекс строки, если вы отправляли несколько строк.")
        private Integer index;

        @Schema(description = "Список из float-чисел — сам embedding.")
        private List<Double> embedding;
    }

    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
