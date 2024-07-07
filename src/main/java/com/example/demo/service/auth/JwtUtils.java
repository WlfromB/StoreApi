package com.example.demo.service.auth;

import com.example.demo.security.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.entities.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {
    
    public static JwtAuthentication generate(Claims claims){
        final JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setRoles(getRoles(claims));
        jwtAuthentication.setLogin(claims.get("login", String.class));
        return jwtAuthentication;
    }
    
    private static Set<Role> getRoles(Claims claims) {
        final List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
