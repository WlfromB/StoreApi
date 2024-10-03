package com.example.demo.controllers;

import com.example.demo.constant.classes.URIStartWith;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.author_book.AuthorBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(URIStartWith.AUTHOR)
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Методы работы с авторами")
public class AuthorController {
    private final AuthorService authorService;
    private final PageableCreator pageableCreator;
    private final AuthorBookService authorBookService;

    private static final String ALL = "/authors";
    private static final String BY_ID = "/{id}";

    @Operation(
            summary = "Получение автора",
            description = "Позволяет получить автора"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<Author> getAuthor(@RequestParam long id) throws Exception {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @Operation(
            summary = "Получение авторов",
            description = "Позволяет получить авторов(страница)"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping(ALL)
    public ResponseEntity<Page<Author>> getAuthors(
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }

    @Operation(
            summary = "Добавлени книги автору",
            description = "Позволяет добавить книгу автору по id"
    )
    @SecurityRequirement(name = "JWT")
    @PatchMapping(BY_ID)
    public ResponseEntity<String> updateAuthor(
            @Valid @RequestBody BookDto book,
            @PathVariable(name = "id") long id) throws Exception {
        authorBookService.addBookToAuthor(book, id);
        return ResponseEntity.ok("Successfully updated author");
    }
}