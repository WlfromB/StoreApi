package com.example.demo.service.author_book;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Book;

import java.util.List;

public interface AuthorBookService {
    Book addBookToAuthor(BookDto bookDto, Long authorId) throws Exception;

    Book addAuthorToBook(Long bookId, List<Long> authorIds) throws Exception;
}
