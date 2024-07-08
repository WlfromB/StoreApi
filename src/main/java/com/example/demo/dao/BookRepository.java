package com.example.demo.dao;

import com.example.demo.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id IN :authorIds")
    Page<Book> findBookByAuthors(@Param("authorIds") List<Long> authors, Pageable pageable);

    Optional<Book> findBookByTitle(String title);
    
    Page<Book> findAll(Pageable pageable);
}
