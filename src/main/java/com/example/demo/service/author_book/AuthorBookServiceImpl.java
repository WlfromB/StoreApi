package com.example.demo.service.author_book;

import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorBookServiceImpl implements AuthorBookService {
    private final BookService bookService;
    private final AuthorService authorService;

    @Override
    @Transactional
    public Book addBookToAuthor(BookDto bookDto, Long authorId) throws Exception {
        Author author = authorService.getAuthorById(authorId);
        Book book;
        try {
            book = bookService.getByTitle(bookDto.getTitle());
        } catch (NotFoundException e) {
            book = bookDto.from();
        }
        book.getAuthors().add(author);
        return book;
    }

    @Transactional
    public Book addAuthorToBook(Long bookId, List<Long> authorIds) throws Exception {
        Book book = bookService.getBookById(bookId);
        for (Long authorId : authorIds) {
            Author author = authorService.getAuthorById(authorId);
            book.getAuthors().add(author);
            author.getBooks().add(book);
        }
        return book;
    }

}
