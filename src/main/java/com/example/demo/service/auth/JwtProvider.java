package com.example.demo.service.auth;

import com.example.demo.constant.classes.JWTClaimName;
import com.example.demo.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.entities.*;

@Slf4j
@Component
public class JwtProvider {    
    @Value("${jwt.access-secret}")
    private String jwtAccessSecretKey;

    @Value("${jwt.refresh-secret}")
    private String jwtRefreshSecretKey;

    // exception types
    private static final String EXPIRED = "Token expired ";
    private static final String UNSUPPORTED = "Token not supported";
    private static final String MALFORMED = "Malformed JWT";
    private static final String INVALID_TOKEN = "Invalid JWT";
    private static final String INVALID_SIGNATURE = "Invalid JWT signature";

    private String setMessagePlace(String exceptionType){
        return exceptionType + " {}";
    }
    
    public String generateAccessToken(@NonNull User user) {
        final LocalDateTime date = LocalDateTime.now();
        final Instant accessExprirationInstant = date.plusMinutes(5)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExprirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(accessExpiration)
                .signWith(createKey(jwtAccessSecretKey), SignatureAlgorithm.HS256)
                .claim(JWTClaimName.ROLES, formRoles(user))
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        final LocalDateTime date = LocalDateTime.now();
        final Instant refreshExprirationInstant = date.plusDays(30)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExprirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(createKey(jwtRefreshSecretKey),SignatureAlgorithm.HS256)
                .compact();
    }
    
    private boolean validateToken(@NonNull String token, @NonNull String secret) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(createBytes(secret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (ExpiredJwtException expEx) {
            log.error(setMessagePlace(EXPIRED), expEx.getMessage());
        } catch (UnsupportedJwtException unsEx) {
            log.error(setMessagePlace(UNSUPPORTED), unsEx.getMessage());
        } catch (MalformedJwtException mjEx) {  
            log.error(setMessagePlace(MALFORMED), mjEx.getMessage());
        } catch (SignatureException sEx) {
            log.error(setMessagePlace(INVALID_SIGNATURE), sEx.getMessage());
        } catch (Exception e) {
            log.error(setMessagePlace(INVALID_TOKEN), e.getMessage());
        }
        return false;        
    }
    
    public boolean validateAccessToken(@NonNull String token) {
        return validateToken(token, jwtAccessSecretKey);
    }
    
    public boolean validateRefreshToken(@NonNull String token) {
        return validateToken(token, jwtRefreshSecretKey);
    }
    
    public Claims getClaims(@NonNull String token,
                            @NonNull String secretKey){
        return Jwts.parserBuilder()
                .setSigningKey(createBytes(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();            
    }
    
    public Claims getAccessClaims(@NonNull String token){
        return getClaims(token, jwtAccessSecretKey);
    }

    public Claims getRefreshClaims(@NonNull String token){
        return getClaims(token, jwtRefreshSecretKey);
    }
    
    private Key createKey(String secret){
        byte[] keyBytes = createBytes(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    private byte[] createBytes(String secret){
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Arrays.copyOf(keyBytes, 32);
    }
    
    private Set<String> formRoles(User user){
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
