package com.example.demo.controllers;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
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

import com.example.demo.entities.Author;
import com.example.demo.service.author.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/author")
@Slf4j
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PageableCreator pageableCreator;

    @GetMapping
    public ResponseEntity<Author> getAuthor(@RequestParam long id) {
        try {
            return ResponseEntity.ok(authorService.getAuthorById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/authors")
    public ResponseEntity<Page<Author>> getAuthors( 
            @ModelAttribute PaginationParams paginationParams) {
        try{
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(authorService.getAllAuthors(pageable));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateAuthor(
            @Valid @RequestBody BookDto book,
            @PathVariable(name = "id") long id) {
        try {
            authorService.addBook(book, id);
            return ResponseEntity.ok("successfully updated author");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}