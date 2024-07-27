package com.example.demo.controllers;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.book.BookService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/book")
@RequiredArgsConstructor
@Tag(name = "Методы работы с книгами")
public class BookController {
    private final BookService bookService;
    private final PageableCreator pageableCreator;

    @Operation(
            summary = "Получение книг",
            description = "Позволяет получить книги (страницу)"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping("/books")
    public ResponseEntity<Page<Book>> getAllBooks(
            @ModelAttribute PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @Operation(
            summary = "Получение книги",
            description = "Позволяет получить книги по id"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<Book> getBook(@RequestParam long id) throws Exception {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @Operation(
            summary = "Получение книг",
            description = "Позволяет получить книги по id авторов"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping("/authors")
    public ResponseEntity<Page<Book>> getBooksByAuthors(
            @RequestParam List<Long> authorId,
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(bookService.getBooksByAuthors(authorId, pageable));
    }

    @Operation(
            summary = "Добавление книги",
            description = "Позволяет добавить книгу"
    )
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookDto book) {
        Book savedBook = bookService.saveBook(book);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBook.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedBook);
    }

    @Operation(
            summary = "Обновление книги",
            description = "Позволяет добавить книге по её id список авторов"
    )
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/{id}/authors")
    public ResponseEntity<Book> updateBook(@RequestBody List<Long> authorIds,
                                           @PathVariable long id) throws Exception {
        return ResponseEntity.ok(bookService.setAuthors(id, authorIds));
    }
}
