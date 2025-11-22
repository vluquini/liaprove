package com.lia.liaprove.infrastructure.exceptions;

import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> createErrorBody(HttpStatus status, Object error, String path) {
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("path", path);
        body.put("error", error);

        return body;
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDataException(
            InvalidUserDataException ex, HttpServletRequest req) {
        log.warn("InvalidUserDataException: {}", ex.getMessage());

        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("MethodArgumentNotValidException: {}", errors);

        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, errors, req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }
}


