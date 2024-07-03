package com.example.demo.service.discount;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import com.example.demo.responses.DiscountsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DiscountService {
    Page<Discount> getDiscountsByBook(Long bookId, Pageable pageable) throws Exception;

    Page<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date, Pageable pageable) throws Exception;

    Page<Discount> getDiscountByAuthor(Long authorId, Pageable pageable) throws Exception;

    Page<Discount> getDiscountsByDate(LocalDate date, Pageable pageable) throws Exception;

    Discount saveDiscount(DiscountDto discount, Book book);
    
    Page<Discount> getAll(Pageable pageable);
}
