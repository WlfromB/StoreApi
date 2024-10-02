package com.example.demo.service.book;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    int ttl = 600; // time in seconds

    Book getBookById(long id) throws Exception;

    Page<Book> getAllBooks(Pageable pageable) throws Exception;

    Book saveBook(BookDto book);

    Page<Book> getBooksByAuthors(List<Long> authorIds, Pageable pageable) throws Exception;

    Book setAuthors(Long bookId, List<Long> authorIds) throws Exception;

    default String getKey(Long id) {
        return "book:%d".formatted(id);
    }

    default String getKey(Pageable pageable) {
        return "all-books:%d:%d".formatted(pageable.getPageNumber(), pageable.getPageSize());
    }

    default String getKey(List<Long> authorIds) {
        return "book-by-authors:%s".formatted(authorIds.toString());
    }
}
