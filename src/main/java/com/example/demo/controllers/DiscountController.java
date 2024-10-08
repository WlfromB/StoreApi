package com.example.demo.controllers;

import com.example.demo.constant.classes.URIStartWith;
import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import com.example.demo.pagination.PageableCreator;
import com.example.demo.pagination.PaginationParams;
import com.example.demo.service.discount.DiscountService;
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
import java.time.LocalDate;

@RestController
@RequestMapping(URIStartWith.DISCOUNT)
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Методы работы со скидками")
public class DiscountController {
    private final DiscountService discountService;
    private final PageableCreator pageableCreator;

    private static final String ALL = "/discounts";
    private static final String BY_ID = "/{id}";
    private static final String BY_AUTHOR="/author";
    private static final String BY_BOOK="/book";
    private static final String BY_DATE="/date";

    @Operation(
            summary = "Получения пользователя по email/login",
            description = "Позволяет получить пользователя по email/login"
    )
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@Valid @RequestBody DiscountDto discountDto) throws Exception {
        Discount discount = discountService.saveDiscount(discountDto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(BY_ID)
                .buildAndExpand(discount.getId())
                .toUri();                
        return ResponseEntity.created(uri).body(discount);
    }

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по id автора"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping(BY_AUTHOR)
    public ResponseEntity<Page<Discount>> getDiscountByAuthorId(
            @RequestParam Long id,
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(discountService.getDiscountByAuthor(id, pageable));
    }

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по дате"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping(BY_DATE)
    public ResponseEntity<Page<Discount>> getDiscountByDate(
            @RequestParam @Parameter(description = "Дата в формате \"yyyy-mm-dd\"",
                    example = "2024-07-18") LocalDate date,
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(discountService.getDiscountsByDate(date, pageable));
    }

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по id книги и дате"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public ResponseEntity<Page<Discount>> getDiscountsByBookAndDate(
            @RequestParam Long bookId,
            @RequestParam @Parameter(description = "Дата в формате \"yyyy-mm-dd\"",
                    example = "2024-07-18") LocalDate date,
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(discountService.getDiscountByBookAndDate(bookId, date, pageable));
    }

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки по id книги"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping(BY_BOOK)
    public ResponseEntity<Page<Discount>> getDiscountByBook(
            @RequestParam Long bookId,
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(discountService.getDiscountsByBook(bookId, pageable));
    }

    @Operation(
            summary = "Получение скидок",
            description = "Позволяет получить скидки (страницу)"
    )
    @SecurityRequirement(name = "JWT")
    @GetMapping(ALL)
    public ResponseEntity<Page<Discount>> getAllDiscounts(
            @ModelAttribute @Parameter(description = "Параметры страницы. " +
                    "Параметры являются необязательными." +
                    " Можно указывать любые вырианты наличия параметров.") PaginationParams paginationParams) throws Exception {
        Pageable pageable = pageableCreator.create(paginationParams);
        return ResponseEntity.ok(discountService.getAll(pageable));
    }
}
