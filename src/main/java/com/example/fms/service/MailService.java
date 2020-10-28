package com.example.fms.service;

public interface MailService {
    Boolean send(String toEmail, String subject, String text);
}
