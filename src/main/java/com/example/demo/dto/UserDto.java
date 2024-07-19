package com.example.demo.dto;

import com.example.demo.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Класс для добавления пользователя")
public class UserDto {

    @NotBlank(message = "Логин не может быть пустым. Минимальный (кол-во символов) - 5, максимальный - 20")
    @Size(min = 5, max = 20)
    private String login;

    @NotBlank(message = "Email не может быть пустым. Пример: oleg@gmail.com")
    @Pattern(regexp = "^[\\w.-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,6}$")
    @Schema(example = "oleg@gmail.com")
    private String email;

    @NotBlank(message = "Не может быть пустым. Минимальный (кол-во символов) - 8, максимальный - 20")
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
