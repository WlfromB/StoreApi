package com.example.demo.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordProviderBcryptImpl implements PasswordProvider {
    public final static PasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Override
    public String getPassword(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean passwordMatches(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }


}
