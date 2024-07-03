package com.example.demo.controllers;

import com.example.demo.entities.Author;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthorService authorService;
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try{
            userService.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @ModelAttribute PaginationParams paginationParams) {
        try{
            Pageable pageable = PageRequest.of(paginationParams.getPage(),
                    paginationParams.getSize(),
                    Sort.by(paginationParams.getSortBy()));
            return ResponseEntity.ok(userService.findAll(pageable));
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam long id) {
        try {   
            return ResponseEntity.ok(userService.findById(id));
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{id}")
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author,
                                               @PathVariable long id) {
        try {
            authorService.saveAuthor(author, id);
            log.info("successfully created author");
            return ResponseEntity.ok(author);
        } catch (Exception e) {
            log.error("error when creating author");
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody Role role) {
        try{
            userService.changeRole(id, role);
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
