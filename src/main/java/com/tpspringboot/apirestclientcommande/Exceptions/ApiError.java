package com.tpspringboot.apirestclientcommande.Exceptions;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApiError {
    private String message ;
    private Integer code ;
    private LocalDateTime timestamp ;

}
