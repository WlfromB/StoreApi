package com.example.demo.service;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import org.graalvm.nativeimage.LogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookServiceImpl implements BookService {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;
    
    @Override
    @Transactional
    public Book getBookById(long id) throws Exception {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new Exception("Book not found");
        }
        return book;
    }

    @Override
    @Transactional
    public List<Book> getAllBooks() throws Exception {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new Exception("No books found");
        }
        return books;
    }

    @Override
    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public List<Book> getBooksByAuthors(List<Long> authorIds) throws Exception {
        List<Book> books = bookRepository.findBookByAuthors(authorIds);
        if (books.isEmpty()) {
            log.info("книги не нашел");
            throw new Exception("No books found");
        }
        return books;
    }
}
