package com.example.demo.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
public class JwtRequest {
    @NotBlank
    private String loginOrEmail;
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
