package com.example.demo.service.author;


import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    public static final int ttl = 600;

    Author getAuthorById(long id) throws Exception;

    Page<Author> getAllAuthors(Pageable pageable) throws Exception;

    Author saveAuthor(AuthorDto author, long userId) throws Exception;

    void addBook(BookDto book, long authorId) throws Exception;
}