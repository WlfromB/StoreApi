package com.example.demo.requests;

import lombok.Data;

@Data
public class JwtRequest {
    private String loginOrEmail;
    private String password;
}
