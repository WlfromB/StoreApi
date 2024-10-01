package com.example.demo.service.book;

import com.example.demo.cache.PageDeserializer;
import com.example.demo.constant.classes.NotFoundConstants;
import com.example.demo.dao.BookRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.service.author.AuthorService;
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
    private final AuthorService authorService;
    private final CacheService cacheService;

    @Override
    @Transactional
    public Book getBookById(long id) throws Exception {
        String key = getKey(id);
        Book book = cacheService.getFromCache(key, new TypeReference<Book>() {
        });
        if (book == null) {
            book = bookRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NotFoundConstants.BOOK));
            cacheService.setToCache(key, book, ttl);
        }
        return book;
    }

    @Override
    @Transactional
    public Page<Book> getAllBooks(Pageable pageable) throws Exception {
        String key = getKey(pageable);
        PageDeserializer<Book> books = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Book>>() {
                });
        if (books == null) {
            books = new PageDeserializer<>(bookRepository.findAll(pageable));
            if (books.isEmpty()) {
                throw new NotFoundException(NotFoundConstants.setMany(NotFoundConstants.BOOK));
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
        String key = getKey(authorIds);
        PageDeserializer<Book> books = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Book>>() {
                });
        if (books == null) {
            books = new PageDeserializer<>(bookRepository.findBookByAuthors(authorIds, pageable));
            if (books.isEmpty()) {
                throw new NotFoundException(NotFoundConstants.setMany(NotFoundConstants.BOOK));
            }
            cacheService.setToCache(key, books, ttl);
        } else {
        }
        return books;
    }

    @Override
    @Transactional
    public Book setAuthors(Long bookId, List<Long> authorIds) throws Exception {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(NotFoundConstants.BOOK));
        for (Long authorId : authorIds) {
            Author author = authorService.getAuthorById(authorId);
            book.getAuthors().add(author);
            author.getBooks().add(book);
        }
        bookRepository.save(book);
        return book;
    }

    private String getKey(Long id) {
        return "book:%d".formatted(id);
    }

    private String getKey(Pageable pageable) {
        return "all-books:%d:%d".formatted(pageable.getPageNumber(), pageable.getPageSize());
    }

    private String getKey(List<Long> authorIds) {
        return "book-by-authors:%s".formatted(authorIds.toString());
    }
}
