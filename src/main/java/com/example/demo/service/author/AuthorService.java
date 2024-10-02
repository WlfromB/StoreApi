package com.example.demo.service.author;


import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    int ttl = 600;

    Author getAuthorById(long id) throws Exception;

    Page<Author> getAllAuthors(Pageable pageable) throws Exception;

    Author saveAuthor(AuthorDto author, long userId) throws Exception;

    void addBook(BookDto book, long authorId) throws Exception;

    default String getKey(Long authorId) {
        return "author:%d".formatted(authorId);
    }

    default String getKey(Pageable pageable) {
        return "authors:%d:%d"
                .formatted(pageable.getPageNumber(), pageable.getPageSize());
    }
}