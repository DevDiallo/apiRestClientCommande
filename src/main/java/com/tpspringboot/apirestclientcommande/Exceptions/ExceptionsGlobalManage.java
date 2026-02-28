package com.tpspringboot.apirestclientcommande.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsGlobalManage {

    @ExceptionHandler(RessourceNotFoundException.class)
    public ResponseEntity<ApiError> HandleNotFound(RessourceNotFoundException e){

        ApiError apiError = new ApiError() ;

        apiError.setMessage(e.getMessage());
        apiError.setCode(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError) ;

    }

    @ExceptionHandler(RessourceAlreadyExist.class)
    public ResponseEntity<ApiError> HandleNotFound(RessourceAlreadyExist e){

        ApiError apiError = new ApiError() ;

        apiError.setMessage(e.getMessage());
        apiError.setCode(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError) ;

    }

}
