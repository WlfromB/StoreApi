package com.example.demo.service.auth;

import com.example.demo.requests.JwtRequest;
import com.example.demo.responses.JwtResponse;
import com.example.demo.security.JwtAuthentication;

public interface AuthService {
    JwtResponse login(JwtRequest request) throws Exception;

    JwtResponse getAccessToken(String refreshToken) throws Exception;

    JwtResponse refresh(String refreshToken) throws Exception;

    JwtAuthentication getAuthInfo();
}
