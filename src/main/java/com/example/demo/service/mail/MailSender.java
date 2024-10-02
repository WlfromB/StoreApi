package com.example.demo.service.mail;

import jakarta.mail.MessagingException;

import java.security.NoSuchAlgorithmException;

public interface MailSender {
    String ACTIVATION_CODE = "Activation code";
    String ACTIVATION_SUCCESS = "Activation success";
    String VERIFY_URI = "/mail/verify";
    
    void sendMail(String to, String subject, String content) throws MessagingException;

    void sendSuccess(String to) throws Exception;

    void sendCode(String to) throws Exception;
}
