package com.example.demo.service.discount;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface DiscountService {
    Page<Discount> getDiscountsByBook(Long bookId, Pageable pageable) throws Exception;

    Page<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date, Pageable pageable) throws Exception;

    Page<Discount> getDiscountByAuthor(Long authorId, Pageable pageable) throws Exception;

    Page<Discount> getDiscountsByDate(LocalDate date, Pageable pageable) throws Exception;

    Discount saveDiscount(DiscountDto discount) throws Exception;

    Page<Discount> getAll(Pageable pageable);
}
