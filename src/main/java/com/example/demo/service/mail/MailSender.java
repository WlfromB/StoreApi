package com.example.demo.service.mail;

import jakarta.mail.MessagingException;

import java.security.NoSuchAlgorithmException;

public interface MailSender {
    static final String ACTIVATION_CODE = "Activation code";
    static final String ACTIVATION_SUCCESS = "Activation success";
    static final String VERIFY_URI = "/mail/verify";
    
    void sendMail(String to, String subject, String content) throws MessagingException;

    void sendSuccess(String to) throws Exception;

    void sendCode(String to) throws NoSuchAlgorithmException, MessagingException;
}
