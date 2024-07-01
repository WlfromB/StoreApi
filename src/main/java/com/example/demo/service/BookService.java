package com.example.demo.service;

import com.example.demo.entities.Book;

import java.util.List;
import java.util.Set;

public interface BookService {
    Book getBookById(long id) throws Exception;

    List<Book> getAllBooks() throws Exception;

    void saveBook(Book book);
    
    List<Book> getBooksByAuthors(List<Long> authorIds) throws Exception;
}
