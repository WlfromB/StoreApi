package com.example.demo.controllers;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.discount.DiscountService;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/discount")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Методы работы со скидками")
public class DiscountController {
    private final DiscountService discountService;
    private final PageableCreator pageableCreator;

    @Operation(
            summary = "Получения пользователя по email/login",
            description = "Позволяет получить пользователя по email/login"
    )
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public ResponseEntity<Discount> discount(@Valid @RequestBody DiscountDto discount) {
        try {
            return ResponseEntity.ok(discountService.saveDiscount(discount));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по id автора"
    )
    @SecurityRequirement(name = "JWT")
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

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по дате"
    )
    @SecurityRequirement(name = "JWT")
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

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по id книги и дате"
    )
    @SecurityRequirement(name = "JWT")
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

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по id книги"
    )
    @SecurityRequirement(name = "JWT")
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

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки (страницу)"
    )
    @SecurityRequirement(name = "JWT")
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
