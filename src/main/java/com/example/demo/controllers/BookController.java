package com.example.demo.controllers;

import com.example.demo.entities.Book;
import com.example.demo.service.AuthorService;
import com.example.demo.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/book")
public class BookController {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            return ResponseEntity.ok(bookService.getAllBooks());
        } catch (Exception e) {
            log.error("Books not found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{index}")
    public ResponseEntity<Book> getBook(@PathVariable long index) {
        try {
            return ResponseEntity.ok(bookService.getBookById(index));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/authors")
    public ResponseEntity<List<Book>> getBooksByAuthors(@RequestBody List<Long> authorIds){
        try{
            return ResponseEntity.ok(bookService.getBooksByAuthors(authorIds));
        }
        catch (Exception e)
        {
            log.error("Books not found");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        try {
            bookService.saveBook(book);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
