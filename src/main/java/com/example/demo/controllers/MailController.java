package com.example.demo.controllers;

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
@RequestMapping("/mail")
public class MailController {
    private final MailSender mailSender;
    private final ActivationCodeCache cache;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestParam("email") String email) throws NoSuchAlgorithmException, MessagingException {
        mailSender.sendCode(email);
        return ResponseEntity.ok("Successfully sent activation code");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestParam("email") String email,
            @RequestParam("code") String code) throws Exception {
        String result = cache.verify(email, code);
        if (Objects.equals(result, ActivationCodeCache.SUCCESS_VERIFY)) {
            mailSender.sendSuccess(email);
            return ResponseEntity.ok(result);
        }
        throw new IllegalArgumentException("Invalid activation code");
    }
}
