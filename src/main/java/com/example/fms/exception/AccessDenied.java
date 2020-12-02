package com.example.fms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccessDenied extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public AccessDenied(String message) {
        super(message);
    }
}