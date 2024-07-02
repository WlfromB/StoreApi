package com.example.demo.service.book;

import com.example.demo.entities.Book;

import java.util.List;

public interface BookService {
    Book getBookById(long id) throws Exception;

    List<Book> getAllBooks() throws Exception;

    void saveBook(Book book);
    
    List<Book> getBooksByAuthors(List<Long> authorIds) throws Exception;
    
    Book setAuthors(Long bookId, List<Long> authorIds) throws Exception;
}
