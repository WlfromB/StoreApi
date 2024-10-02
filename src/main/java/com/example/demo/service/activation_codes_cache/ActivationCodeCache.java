package com.example.demo.service.activation_codes_cache;

import java.security.NoSuchAlgorithmException;

public interface ActivationCodeCache {
    String SUCCESS_SET = "Successfully set code";
    String SUCCESS_VERIFY = "Successfully verify";
    String ERROR_VERIFY = "Successfully verify";
    Long TTL = 900L;
    
    String generateVerificationCode(String userLogin) throws NoSuchAlgorithmException;

    String setVerificationCode(String userLogin, String verificationCode) throws NoSuchAlgorithmException;

    String verify(String userLogin, String verificationCode) throws NoSuchAlgorithmException;
}
