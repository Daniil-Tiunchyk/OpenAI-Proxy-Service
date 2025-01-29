package com.example.proxyapi.controller;

import com.example.proxyapi.dto.ErrorResponse;
import com.example.proxyapi.exception.ProxyApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Глобальный обработчик исключений для приложения.
 * Возвращает структурированные ответы об ошибках в формате JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обработка ProxyApiException.
     *
     * @param e Исключение ProxyApiException
     * @return Структурированный ответ об ошибке
     */
    @ExceptionHandler(ProxyApiException.class)
    public ResponseEntity<ErrorResponse> handleProxyApiException(ProxyApiException e) {
        log.error("ProxyApiException: {}", e.getMessage(), e);
        ErrorResponse body = new ErrorResponse("ProxyApiException", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обработка IllegalArgumentException.
     *
     * @param e Исключение IllegalArgumentException
     * @return Структурированный ответ об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage(), e);
        ErrorResponse body = new ErrorResponse("IllegalArgumentException", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка всех остальных исключений.
     *
     * @param e Исключение
     * @return Структурированный ответ об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        ErrorResponse body = new ErrorResponse("InternalError", "Внутренняя ошибка сервера.");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<String> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("Unsupported media type: " + ex.getContentType());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("File too large!");
    }
}
