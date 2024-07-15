package com.example.demo.controllers;

import com.example.demo.requests.JwtRequest;
import com.example.demo.requests.RefreshRequest;
import com.example.demo.responses.JwtResponse;
import com.example.demo.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Методы работы с авторизацией")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Авторизация пользователя",
            description = "Позволяет получить access и refresh jwts"
    )
    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Получение access токена",
            description = "Позволяет обновить access токен, в случае валидного и not exprired refresh токена"
    )
    @PostMapping("access")
    public ResponseEntity<JwtResponse> access(@RequestBody RefreshRequest request) {
        try {
            return ResponseEntity.ok(authService.getAccessToken(request.getToken()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @Operation(
            summary = "Получение refresh токена",
            description = "Позволяет обновить refresh токен, в случае валидного и not exprired refresh токена"
    )
    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            return ResponseEntity.ok(authService.refresh(request.getToken()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
