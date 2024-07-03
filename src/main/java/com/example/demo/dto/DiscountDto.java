package com.example.demo.dto;

import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import com.example.demo.service.book.BookService;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Component
public class DiscountDto {
    @FutureOrPresent
    private LocalDate dateOfSaleStart;

    @Future
    private LocalDate dateOfSaleEnd;

    @DecimalMax("99.9")
    @DecimalMin("0.01")
    @Digits(integer = 2, fraction = 2)
    private BigDecimal dimensionOfSale;

    @NotNull
    private Long bookId;
    
    @Autowired
    private BookService bookService;

    public Discount from(Book book) 
    {
        Discount discount = new Discount();
        discount.setDateOfSaleStart(this.getDateOfSaleStart());
        discount.setDateOfSaleEnd(this.getDateOfSaleEnd());
        discount.setDimensionOfSale(this.getDimensionOfSale());
        discount.setBook(book);
        return discount;
    }
    
}
