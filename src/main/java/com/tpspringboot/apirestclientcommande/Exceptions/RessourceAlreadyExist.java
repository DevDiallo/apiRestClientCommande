package com.tpspringboot.apirestclientcommande.Exceptions;

public class RessourceAlreadyExist extends RuntimeException{
    public RessourceAlreadyExist(String message){
        super(message);
    }
}
