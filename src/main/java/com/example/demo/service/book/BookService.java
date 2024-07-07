package com.example.demo.service.book;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Book getBookById(long id) throws Exception;

    Page<Book> getAllBooks(Pageable pageable) throws Exception;

    Book saveBook(BookDto book);

    Page<Book> getBooksByAuthors(List<Long> authorIds, Pageable pageable) throws Exception;

    Book setAuthors(Long bookId, List<Long> authorIds) throws Exception;
}
