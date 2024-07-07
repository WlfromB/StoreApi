package com.example.demo.security;

public interface PasswordProvider {
    String getPassword(String password);

    boolean passwordMatches(String password, String hashedPassword);
}
