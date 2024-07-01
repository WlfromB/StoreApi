package com.example.demo.service;

import com.example.demo.dao.BookRepository;
import com.example.demo.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

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
}
