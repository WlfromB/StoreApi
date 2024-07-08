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
public class UserController {
    private final UserService userService;
    private final AuthorService authorService;
    private final PageableCreator pageableCreator;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto user) {
        try {
            return ResponseEntity.ok(userService.save(user));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

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

    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody AuthorDto author,
                                               @PathVariable long id) {
        try {
            return ResponseEntity.ok(authorService.saveAuthor(author, id));
        } catch (Exception e) {
            log.error("error when creating author");
            return ResponseEntity.badRequest().build();
        }
    }

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
