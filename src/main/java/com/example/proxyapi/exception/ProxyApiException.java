package com.example.proxyapi.exception;

/**
 * Исключение для ошибок, связанных с ProxyAPI.
 */
public class ProxyApiException extends RuntimeException {

    public ProxyApiException(String message) {
        super(message);
    }

    public ProxyApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
