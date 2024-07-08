package com.example.demo.controllers;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.discount.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/discount")
@Slf4j
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;
    private final PageableCreator pageableCreator;

    @PostMapping
    public ResponseEntity<Discount> discount(@Valid @RequestBody DiscountDto discount) {
        try {
            return ResponseEntity.ok(discountService.saveDiscount(discount));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/author")
    public ResponseEntity<Page<Discount>> getDiscountByAuthorId(
            @RequestParam Long id,
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(discountService.getDiscountByAuthor(id, pageable));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/date")
    public ResponseEntity<Page<Discount>> getDiscountByDate(
            @RequestParam LocalDate date,
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(discountService.getDiscountsByDate(date, pageable));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<Discount>> getDiscountsByBookAndDate(
            @RequestParam Long bookId,
            @RequestParam LocalDate date,
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(discountService.getDiscountByBookAndDate(
                    bookId, date, pageable));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/book")
    public ResponseEntity<Page<Discount>> getDiscountByBook(
            @RequestParam Long bookId,
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(discountService.getDiscountsByBook(bookId, pageable));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/discounts")
    public ResponseEntity<Page<Discount>> getAllDiscounts(
            @ModelAttribute PaginationParams paginationParams) {
        try {
            Pageable pageable = pageableCreator.create(paginationParams);
            return ResponseEntity.ok(discountService.getAll(pageable));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
