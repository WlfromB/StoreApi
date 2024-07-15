package com.example.demo.controllers;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.author.AuthorService;
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
@RequestMapping("/author")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Методы работы с авторами")
public class AuthorController {
    private final AuthorService authorService;
    private final PageableCreator pageableCreator;

    @Operation(
            summary = "Получение автора",
            description = "Позволяет получить автора"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<Author> getAuthor(@RequestParam long id) {
        try {
            return ResponseEntity.ok(authorService.getAuthorById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Получение авторов",
            description = "Позволяет получить авторов(страница)"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping("/authors")
    public ResponseEntity<Page<Author>> getAuthors(
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(authorService.getAllAuthors(pageable));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Добавлени книги автору",
            description = "Позволяет добавить книгу автору по id"
    )
    @SecurityRequirement(name = "JWT")
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