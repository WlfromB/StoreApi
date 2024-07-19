package com.example.demo.service.book;

import com.example.demo.cache.PageDeserializer;
import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.service.cache.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CacheService cacheService;
    private final int ttl = 600; // time in seconds 

    @Override
    @Transactional
    public Book getBookById(long id) throws Exception {
        String key = "book:%d".formatted(id);
        Book book = cacheService.getFromCache(key, new TypeReference<Book>() {
        });
        if (book == null) {
            book = bookRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Book not found"));
            cacheService.setToCache(key, book, ttl);
        }
        return book;
    }

    @Override
    @Transactional
    public Page<Book> getAllBooks(Pageable pageable) throws Exception {
        String key = "all-books:%d:%d".formatted(pageable.getPageNumber(), pageable.getPageSize());
        PageDeserializer<Book> books = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Book>>() {
                });
        if (books == null) {
            books = new PageDeserializer<>(bookRepository.findAll(pageable));
            if (books.isEmpty()) {
                throw new NotFoundException("No books found");
            }
            cacheService.setToCache(key, books, ttl);
        } else {
        }
        return books;
    }

    @Override
    @Transactional
    public Book saveBook(BookDto book) {
        return bookRepository.save(book.from());
    }

    @Override
    @Transactional
    public Page<Book> getBooksByAuthors(List<Long> authorIds, Pageable pageable) throws Exception {
        String key = "book-by-authors:%s".formatted(authorIds.toString());
        PageDeserializer<Book> books = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Book>>() {
                });
        if (books == null) {
            books = new PageDeserializer<>(bookRepository.findBookByAuthors(authorIds, pageable));
            if (books.isEmpty()) {
                throw new NotFoundException("No books found");
            }
            cacheService.setToCache(key, books, ttl);
        } else {
        }
        return books;
    }

    @Override
    @Transactional
    public Book setAuthors(Long bookId, List<Long> authorIds) throws Exception {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
        for (Long authorId : authorIds) {
            Author author = authorRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Author not found"));
            book.getAuthors().add(author);
            author.getBooks().add(book);
        }
        bookRepository.save(book);
        return book;
    }

}
