package com.example.demo.service.auth;

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

@Slf4j
@Component
public class JwtProvider {    
    @Value("${jwt.access-secret}")
    private String jwtAccessSecretKey;

    @Value("${jwt.refresh-secret}")
    private String jwtRefreshSecretKey;
    
    public String generateAccessToken(@NonNull User user) {
        final LocalDateTime date = LocalDateTime.now();
        final Instant accessExprirationInstant = date.plusMinutes(5)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExprirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(accessExpiration)
                .signWith(createKey(jwtAccessSecretKey), SignatureAlgorithm.HS256)
                .claim("roles", user.getRole())
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
                .claim("role", user.getRole())
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
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
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
}
