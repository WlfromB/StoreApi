package com.example.demo.service.auth;

import com.example.demo.entities.User;
import com.example.demo.requests.JwtRequest;
import com.example.demo.responses.JwtResponse;
import com.example.demo.security.JwtAuthentication;
import com.example.demo.security.PasswordProvider;
import com.example.demo.service.user.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordProvider passwordProvider;
    private final JwtProvider jwtProvider;


    @Override
    public JwtResponse login(JwtRequest request) throws Exception {
        final User user = userService.findByEmailOrLogin(request.getLoginOrEmail());
        if (passwordProvider.passwordMatches(request.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            return new JwtResponse(accessToken, refreshToken);
        }
        throw new AccessDeniedException("Invalid password");
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) throws Exception {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final User user = userService.findByEmailOrLogin(login);
            final String accessToken = jwtProvider.generateAccessToken(user);
            return new JwtResponse(accessToken, null);
        }
        throw new AccessDeniedException("Invalid refresh token");
    }

    @Override
    public JwtResponse refresh(String refreshToken) throws Exception {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final User user = userService.findByEmailOrLogin(login);
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String newRefreshToken = jwtProvider.generateRefreshToken(user);
            return new JwtResponse(accessToken, newRefreshToken);
        }
        throw new AccessDeniedException("Invalid refresh token");
    }

    @Override
    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
