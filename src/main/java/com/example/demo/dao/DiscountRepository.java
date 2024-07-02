package com.example.demo.dao;

import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findDiscountByBook(Book book);

    Optional<List<Discount>> findDiscountByDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
            (LocalDate date1, LocalDate date2);

    Optional<List<Discount>> findDiscountByAuthors(Set<Author> authors);

    Optional<List<Discount>> findDiscountByBookAndDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
            (Book book, LocalDate date1, LocalDate date2);
}
