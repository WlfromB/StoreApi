package com.example.demo.controllers;

import com.example.demo.constant.classes.URIStartWith;
import com.example.demo.service.activation_codes_cache.ActivationCodeCache;
import com.example.demo.service.mail.MailSender;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(URIStartWith.MAIL)
public class MailController {
    private final MailSender mailSender;
    private final ActivationCodeCache cache;

    // URIs
    private static final String SEND_CODE = "/send-code";
    private static final String VERIFY = "/verify";
    // Params
    private static final String EMAIL_PARAM = "email";
    private static final String CODE_PARAM = "code";
    // Answers
    private static final String SUCCESS_SEND = "Successfully sent activation code";
    private static final String WRONG_CODE = "Invalid activation code";


    @PostMapping(SEND_CODE)
    public ResponseEntity<String> sendCode(@RequestParam(EMAIL_PARAM) String email) throws Exception {
        mailSender.sendCode(email);
        return ResponseEntity.ok(SUCCESS_SEND);
    }

    @GetMapping(VERIFY)
    public ResponseEntity<String> verify(
            @RequestParam(EMAIL_PARAM) String email,
            @RequestParam(CODE_PARAM) String code) throws Exception {
        String result = cache.verify(email, code);
        if (Objects.equals(result, ActivationCodeCache.SUCCESS_VERIFY)) {
            mailSender.sendSuccess(email);
            return ResponseEntity.ok(result);
        }
        throw new IllegalArgumentException(WRONG_CODE);
    }
}
