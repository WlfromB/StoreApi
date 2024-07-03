package com.example.demo.controllers;

import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    
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
    public ResponseEntity<List<User>> getAllUsers() {
        try{
            return ResponseEntity.ok(userService.findAll());
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
