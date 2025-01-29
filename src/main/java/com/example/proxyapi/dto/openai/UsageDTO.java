package com.example.proxyapi.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO для поля "usage" в Chat Completion Response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageDTO {

    @Schema(description = "Количество токенов, использованных для промпта", example = "57")
    private int prompt_tokens;

    @Schema(description = "Количество токенов, использованных для завершения", example = "17")
    private int completion_tokens;

    @Schema(description = "Общее количество использованных токенов", example = "74")
    private int total_tokens;
}
