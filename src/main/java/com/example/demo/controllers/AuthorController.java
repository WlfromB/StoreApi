package com.example.demo.controllers;

import com.example.demo.entities.Book;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Author;
import com.example.demo.service.author.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/author")
@Slf4j
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/{index}")
    public ResponseEntity<Author> getAuthor(@PathVariable(name = "index") long id) {
        try {
            return ResponseEntity.ok(authorService.getAuthorById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAuthors() {
        try {
            return ResponseEntity.ok(authorService.getAllAuthors());
        } catch (Exception e) {
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
    public ResponseEntity<String> updateAuthor(
            @Valid @RequestBody Book book,
            @PathVariable(name = "id") long id) {
        try {
            authorService.addBook(book, id);
            return ResponseEntity.ok("successfully updated author");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}