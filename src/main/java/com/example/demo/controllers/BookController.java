package com.example.demo.controllers;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.book.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Value;
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
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private PageableCreator pageableCreator;

    @GetMapping("/books")
    public ResponseEntity<Page<Book>> getAllBooks(
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(bookService.getAllBooks(pageable));
        } catch (Exception e) {
            log.error("Books not found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Book> getBook(@RequestParam long id) {
        try {
            return ResponseEntity.ok(bookService.getBookById(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/authors")
    public ResponseEntity<Page<Book>> getBooksByAuthors(
            @RequestParam List<Long> authorId,
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(bookService.getBooksByAuthors(authorId, pageable));
        } catch (Exception e) {
            log.error("Books not found");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookDto book) {
        try {
            return ResponseEntity.ok(bookService.saveBook(book));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/authors")
    public ResponseEntity<Book> updateBook(@RequestBody List<Long> authorIds,
                                           @PathVariable long id) {
        try {
            return ResponseEntity.ok(bookService.setAuthors(id, authorIds));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
