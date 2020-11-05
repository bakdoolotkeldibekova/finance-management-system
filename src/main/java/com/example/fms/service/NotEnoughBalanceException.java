package com.example.fms.service;

public class NotEnoughBalanceException extends Exception{
    public NotEnoughBalanceException(String message) {
        super(message);
    }
}

