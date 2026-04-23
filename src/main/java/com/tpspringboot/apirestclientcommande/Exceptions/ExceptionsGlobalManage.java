package com.tpspringboot.apirestclientcommande.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionsGlobalManage {

    @ExceptionHandler(RessourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(RessourceNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(RessourceAlreadyExist.class)
    public ResponseEntity<ApiError> handleAlreadyExists(RessourceAlreadyExist e) {
        return build(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Validation error", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedBody(HttpMessageNotReadableException e) {
        return build(HttpStatus.BAD_REQUEST, "Malformed request body", e.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException e) {
        return build(HttpStatus.FORBIDDEN, "Access denied", e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException e) {
        return build(HttpStatus.UNAUTHORIZED, "Authentication required", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception e) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage() != null ? e.getMessage() : "Erreur interne du serveur",
                Map.of("exception", e.getClass().getSimpleName())
        );
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Object details) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), message, details));
    }
}