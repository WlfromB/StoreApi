package com.example.demo.controllers;

import com.example.demo.dao.DiscountRepository;
import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import com.example.demo.service.book.BookService;
import com.example.demo.service.discount.DiscountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/discount")
@Slf4j
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @Autowired
    private BookService bookService;
    
    @PostMapping
    public ResponseEntity<Discount> discount(@Valid @RequestBody DiscountDto discount) {
        try {
            return ResponseEntity.ok(discountService.saveDiscount(discount,
                    bookService.getBookById(discount.getBookId())));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/author")
    public ResponseEntity<List<Discount>> getDiscountByAuthorId(@RequestParam Long id) {
        try {
            return ResponseEntity.ok(discountService.getDiscountByAuthor(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date")
    public ResponseEntity<List<Discount>> getDiscountByDate(@RequestParam LocalDate date) {
        try {
            return ResponseEntity.ok(discountService.getDiscountsByDate(date));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Discount>> getDiscountsByBookAndDate(@RequestParam Long bookId
            , @RequestParam LocalDate date) {
        try {
            return ResponseEntity.ok(discountService.getDiscountByBookAndDate(bookId, date));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/book")
    public ResponseEntity<List<Discount>> getDiscountByBook(@RequestParam Long bookId) {
        try {
            return ResponseEntity.ok(discountService.getDiscountsByBook(bookId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /*@GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {}*/
}
