package com.example.demo.service.discount;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface DiscountService {
    int ttl = 900;

    Page<Discount> getDiscountsByBook(Long bookId, Pageable pageable) throws Exception;

    Page<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date, Pageable pageable) throws Exception;

    Page<Discount> getDiscountByAuthor(Long authorId, Pageable pageable) throws Exception;

    Page<Discount> getDiscountsByDate(LocalDate date, Pageable pageable) throws Exception;

    Discount saveDiscount(DiscountDto discount) throws Exception;

    Page<Discount> getAll(Pageable pageable) throws Exception;


    default String getKey(Pageable pageable) {
        return "discount:%d:%d"
                .formatted(pageable.getPageNumber(), pageable.getPageSize());
    }

    default String getKey(LocalDate date, Pageable pageable) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "discountByDate:%s:%d:%d"
                .formatted(dtf.format(date), pageable.getPageNumber(), pageable.getPageSize());
    }

    default String getKey(String entityName, Long authorId, Pageable pageable) {
        return "discountBy%s:%d:%d:%d"
                .formatted(entityName, authorId, pageable.getPageNumber(), pageable.getPageSize());
    }

    default String getKey(Long bookId, LocalDate date, Pageable pageable) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "discountByBookAndDate:%d:%s:%d:%d"
                .formatted(bookId, dtf.format(date), pageable.getPageNumber(), pageable.getPageSize());
    }

}
