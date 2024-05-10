package com.github.thiagomarqs.gerenciamentopessoas.controller.exception;

import java.time.LocalDateTime;

public final class ExceptionResponse {

    private final String message;
    private final int status;
    private final String path;
    private final LocalDateTime timestamp;

    public ExceptionResponse(String message, int status, String path, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
    }

    public static ExceptionResponse of(Exception exception, int statusCode, String path) {
        return new ExceptionResponse(exception.getMessage(), statusCode, path, LocalDateTime.now());
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
