package com.example.demo.service.book;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public Book getBookById(long id) throws Exception {
        return bookRepository.findById(id)
                .orElseThrow(()->new Exception("Book not found"));
    }

    @Override
    @Transactional
    public Page<Book> getAllBooks(Pageable pageable) throws Exception {
        Page<Book> books = bookRepository.findAll(pageable);
        if (books.isEmpty()) {
            throw new Exception("No books found");
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
        Page<Book> books = bookRepository.findBookByAuthors(authorIds, pageable);
        if (books.isEmpty()) {
            throw new Exception("No books found");
        }
        return books;
    }

    @Override
    @Transactional
    public Book setAuthors(Long bookId, List<Long> authorIds) throws Exception {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new Exception("Book not found"));
        for (Long authorId : authorIds) {
            Author author = authorRepository.findById(authorId).orElseThrow(() -> new Exception("Author not found"));
            book.getAuthors().add(author);
            author.getBooks().add(book);
        }
        bookRepository.save(book);
        return book;
    }

}
