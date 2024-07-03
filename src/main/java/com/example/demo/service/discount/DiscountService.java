package com.example.demo.service.discount;

import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import com.example.demo.responses.DiscountsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DiscountService {
    List<Discount> getDiscountsByBook(Long bookId) throws Exception;

    List<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date) throws Exception;

    List<Discount> getDiscountByAuthor(Long authorId) throws Exception;

    List<Discount> getDiscountsByDate(LocalDate date) throws Exception;

    Discount saveDiscount(DiscountDto discount, Book book);
}
