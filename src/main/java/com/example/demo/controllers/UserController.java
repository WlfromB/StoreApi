package com.example.demo.controllers;

import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Методы работы с пользователями")
public class UserController {
    private final UserService userService;
    private final AuthorService authorService;
    private final PageableCreator pageableCreator;

    @Operation(
            summary = "Создание пользователя",
            description = "Позволяет зарегистрировать пользователя"
    )
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto user) {
        try {
            return ResponseEntity.ok(userService.save(user));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Получение пользователей",
            description = "Позволяет страницу пользователей"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(userService.findAll(pageable));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(
            summary = "Получение пользователя",
            description = "Позволяет получить пользователя по id"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Регистрация автора",
            description = "Позволяет зарегистрировать автора по id пользователя"
    )
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{id}")
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody AuthorDto author,
                                               @PathVariable long id) {
        try {
            return ResponseEntity.ok(authorService.saveAuthor(author, id));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            log.error("error when creating author");
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Смена данных пользователя",
            description = "Позволяет изменить роль пользователя"
    )
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id,
                                           @RequestBody Role role) {
        try {
            userService.changeRole(id, role);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Получения пользователя по email/login",
            description = "Позволяет получить пользователя по email/login"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping("/email-or-login")
    public ResponseEntity<User> getUserByEmailOrLogin(@RequestParam(name = "email_or_login") String emailOrLogin) {
        try {
            return ResponseEntity.ok(userService.findByEmailOrLogin(emailOrLogin));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
