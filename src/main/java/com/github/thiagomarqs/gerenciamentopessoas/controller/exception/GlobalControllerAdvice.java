package com.github.thiagomarqs.gerenciamentopessoas.controller.exception;

import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        var statusCode = 500;
        var path = request.getRequestURI();

        ExceptionResponse response = ExceptionResponse.of(exception, statusCode, path);

        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleBusinessRuleException(BusinessRuleException exception, HttpServletRequest request) {
        var statusCode = 400;
        var path = request.getRequestURI();

        ExceptionResponse response = ExceptionResponse.of(exception, statusCode, path);

        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleEntityNotFound(EntityNotFoundException exception, HttpServletRequest request) {
        var statusCode = 404;
        var path = request.getRequestURI();

        ExceptionResponse response = ExceptionResponse.of(exception, statusCode, path);

        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

}
