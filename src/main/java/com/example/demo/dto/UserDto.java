package com.example.demo.dto;

import com.example.demo.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank
    @Size(min = 5, max = 20)
    private String login;

    @NotBlank
    @Pattern(regexp = "^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    public User from() {
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setAuthor(null);
        return user;
    }
}
