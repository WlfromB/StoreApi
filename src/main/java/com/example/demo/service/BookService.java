package com.example.demo.service;

import com.example.demo.entities.Book;

import java.util.List;

public interface BookService {
    Book getBookById(long id) throws Exception;

    List<Book> getAllBooks() throws Exception;

    void saveBook(Book book);
}
