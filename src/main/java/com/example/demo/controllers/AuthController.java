package com.example.demo.controllers;

import com.example.demo.constant.classes.URIStartWith;
import com.example.demo.requests.JwtRequest;
import com.example.demo.requests.RefreshRequest;
import com.example.demo.responses.JwtResponse;
import com.example.demo.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URIStartWith.AUTH)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Методы работы с авторизацией")
public class AuthController {
    private final AuthService authService;
    private static final String LOGIN="/login";
    private static final String REFRESH="/refresh";
    private static final String ACCESS ="/access";

    @Operation(
            summary = "Авторизация пользователя",
            description = "Позволяет получить access и refresh jwts"
    )
    @PostMapping(LOGIN)
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest request) throws Exception {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Получение access токена",
            description = "Позволяет обновить access токен, в случае валидного и not exprired refresh токена"
    )
    @PostMapping(ACCESS)
    public ResponseEntity<JwtResponse> access(@Valid @RequestBody RefreshRequest request) throws Exception {
        return ResponseEntity.ok(authService.getAccessToken(request.getToken()));
    }

    @Operation(
            summary = "Получение refresh токена",
            description = "Позволяет обновить refresh токен, в случае валидного и not exprired refresh токена"
    )
    @PostMapping(REFRESH)
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshRequest request) throws Exception {
        return ResponseEntity.ok(authService.refresh(request.getToken()));
    }
}
