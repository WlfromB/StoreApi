package com.example.demo.service;


import com.example.demo.entities.Author;
import com.example.demo.entities.Book;

import java.util.List;

public interface AuthorService {
    Author getAuthorById(long id) throws Exception;

    List<Author> getAllAuthors() throws Exception;

    void saveAuthor(Author author);

    void addBook(Book book, long authorId) throws Exception;
    //   void saveAuthor(AuthorCreateRequest request);
}