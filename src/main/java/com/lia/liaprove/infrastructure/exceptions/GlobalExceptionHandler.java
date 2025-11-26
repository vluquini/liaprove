package com.lia.liaprove.infrastructure.exceptions;

import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.InvalidCredentialsException;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
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

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(
            InvalidCredentialsException ex, HttpServletRequest req) {
        log.warn("InvalidCredentialsException: {}", ex.getMessage());

        Map<String, Object> body = createErrorBody(HttpStatus.UNAUTHORIZED, "Unauthorized: " + ex.getMessage(), req.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUserDataException(
            InvalidUserDataException ex, HttpServletRequest req) {
        log.warn("InvalidUserDataException: {}", ex.getMessage());

        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationException(
            AuthorizationException ex, HttpServletRequest req) {
        log.warn("AuthorizationException: {}", ex.getMessage());

        Map<String, Object> body = createErrorBody(HttpStatus.FORBIDDEN, "Forbidden: " + ex.getMessage(), req.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(
        UserNotFoundException ex, HttpServletRequest req) {
        log.warn("UserNotFoundException: {}", ex.getMessage());
        Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND, "Not Found: " + ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
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


