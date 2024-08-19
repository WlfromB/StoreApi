package com.example.demo.service.activation_codes_cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@Slf4j
public class ActivationCodeCacheServiceImpl implements ActivationCodeCache{
    @Autowired
    @Qualifier("verificationJedisPool")
    private JedisPool jedisPool;
    
    public String generateVerificationCode(String userEmail) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(userEmail.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(hash);
    }
    
    public String setVerificationCode(String userLogin, String verificationCode) throws NoSuchAlgorithmException {
        Jedis jedis = jedisPool.getResource();
        String key = "login:%s".formatted(userLogin);
        jedis.set(key.getBytes(StandardCharsets.UTF_8), verificationCode.getBytes(StandardCharsets.UTF_8));
        return SUCCESS_SET;
    }

    public String verify(String userLogin, String verificationCode) throws NoSuchAlgorithmException {
        Jedis jedis = jedisPool.getResource();
        String key = "login:%s".formatted(userLogin);
        if (jedis.get(key.getBytes(StandardCharsets.UTF_8)).equals(verificationCode.getBytes(StandardCharsets.UTF_8))){
            return SUCCESS_VERIFY;
        }
        return ERROR_VERIFY;
    }
}
