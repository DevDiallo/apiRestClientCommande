package com.tpspringboot.apirestclientcommande.Exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String code;
    private String message;
    private Object details;
    private LocalDateTime timestamp;

    public static ApiError of(String code, String message, Object details) {
        ApiError apiError = new ApiError();
        apiError.setCode(code);
        apiError.setMessage(message);
        apiError.setDetails(details);
        apiError.setTimestamp(LocalDateTime.now());
        return apiError;
    }

    public static ApiError of(Integer httpCode, String message, Object details) {
        return of("HTTP_" + httpCode, message, details);
    }

}
