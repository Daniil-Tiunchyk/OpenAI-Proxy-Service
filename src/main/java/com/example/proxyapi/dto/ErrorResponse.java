package com.example.proxyapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO для ошибок.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorResponse {

    private String error;
    private String message;
}
