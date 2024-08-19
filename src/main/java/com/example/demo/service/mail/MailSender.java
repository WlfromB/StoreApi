package com.example.demo.service.mail;

import java.security.NoSuchAlgorithmException;

public interface MailSender {
    static final String ACTIVATION_CODE = "Activation code";
    static final String ACTIVATION_SUCCESS = "Activation success";

    void sendMail(String to, String subject, String content);

    void sendSuccess(String to) throws Exception;

    void sendCode(String to) throws NoSuchAlgorithmException;
}
