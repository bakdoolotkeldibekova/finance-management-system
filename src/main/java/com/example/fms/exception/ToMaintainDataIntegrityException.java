package com.example.fms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ToMaintainDataIntegrityException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ToMaintainDataIntegrityException(String message){
        super(message);
    }
}
