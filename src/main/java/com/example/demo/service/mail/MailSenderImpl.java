package com.example.demo.service.mail;

import com.example.demo.service.activation_codes_cache.ActivationCodeCache;
import com.example.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
    private final JavaMailSender javaMailSender;
    private final ActivationCodeCache cache;
    private final UserService userService;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }

    public void sendCode(String to) throws NoSuchAlgorithmException {
        String subject = ACTIVATION_CODE;
        String content = cache.generateVerificationCode(to);
        cache.setVerificationCode(to, content);
        sendMail(to, subject, generateContentCode(to, content));
    }

    private String generateContentCode(String to, String code) {
        return String.format("http://localhost:8080/mail/verify?email=%s&code=%s", to, code);
    }

    public void sendSuccess(String to) throws Exception {
        String subject = ACTIVATION_SUCCESS;
        String content = ACTIVATION_SUCCESS;
        sendMail(to, subject, content);
        userService.setActivatedMail(to);
    }
}
