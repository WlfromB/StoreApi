package com.example.demo.dao;

import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Page<Discount> findDiscountsByBook(Book book, Pageable pageable);

    Page<Discount> findDiscountByDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
            (LocalDate date1, LocalDate date2, Pageable pageable);

    @Query("select d from Discount d where d.book in (select b from " +
            "Book b join b.authors a where a =:author)")
    Page<Discount> findDiscountByAuthor(Author author, Pageable pageable);

    Page<Discount> findDiscountByBookAndDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
            (Book book, LocalDate date1, LocalDate date2, Pageable pageable);
    
    Page<Discount> findAll(Pageable pageable); 
}
