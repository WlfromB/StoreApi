package com.example.demo.service.auth;

import com.example.demo.security.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setRoles(getRoles(claims));
        jwtAuthentication.setLogin(claims.get("login", String.class));
        return jwtAuthentication;
    }

    private static Set<String> getRoles(Claims claims) {
        List<String> rolesList = claims.get("roles", List.class);
        return rolesList != null ? new HashSet<>(rolesList) : Collections.emptySet();
    }
}
