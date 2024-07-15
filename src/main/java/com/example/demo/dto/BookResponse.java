package com.example.demo.dto;

import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Hidden
public class BookResponse {
    private String title;
    private String description;
    private int numberPages;
    private LocalDate dateCreated;
    private Set<Author> authors = new HashSet<>();
    private BigDecimal price;
    
    public BookResponse(Book book) {
        this.title = book.getTitle();
        this.description = book.getDescription();
        this.numberPages = book.getNumberPages();
        this.dateCreated = book.getDateCreated();
        this.price = book.getPrice();
        this.authors.addAll(book.getAuthors());
    }
}
