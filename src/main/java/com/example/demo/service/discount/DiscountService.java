package com.example.demo.service.discount;

import com.example.demo.entities.Author;
import com.example.demo.entities.Discount;
import com.example.demo.responses.DiscountsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DiscountService {
    Discount getDiscountForBook(Long bookId) throws Exception;
    List<Discount> getDiscountForBookByDate(Long bookId, LocalDate date) throws Exception;
    List<Discount> getDiscountByAuthors(Set<Author> authors) throws Exception;
    List<Discount> getDiscountsByDate(LocalDate date) throws Exception;
    Discount saveDiscounts(Discount discount) throws Exception;
}
