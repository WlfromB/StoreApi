package com.example.demo.dao;

import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id IN :authorIds")
    List<Book> findBookByAuthors(@Param("authorIds")List<Long> authors);

    Book findBookByTitle(String title);
}
