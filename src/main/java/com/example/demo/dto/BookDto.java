package com.example.demo.dto;

import com.example.demo.entities.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Класс для добавления книги")
public class BookDto {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Min(1)
    private int numberPages;

    @PastOrPresent
    private LocalDate dateCreated;

    @Digits(integer = 10, fraction = 2)
    @DecimalMin("0.01")
    private BigDecimal price;

    public Book from() {
        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);
        book.setNumberPages(numberPages);
        book.setDateCreated(dateCreated);
        book.setPrice(price);
        return book;
    }

}
