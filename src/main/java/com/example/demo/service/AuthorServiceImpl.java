package com.example.demo.service;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.entities.Author;

import com.example.demo.entities.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    @Transactional
    public Author getAuthorById(long id) throws Exception {
        Author author = authorRepository.findById(id).get();
        if (author == null) {
            throw new Exception("Author not found");
        }
        return author;
    }

    @Override
    public List<Author> getAllAuthors() throws Exception {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            throw new Exception("Authors not found");
        }
        return authors;
    }

    @Override
    @Transactional
    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }

    @Override
    @Transactional
    public void addBook(Book book, long authorId) throws Exception {
        Author author = getAuthorById(authorId);
        Book addedBook = bookRepository.findBookByTitle(book.getTitle());
        if (addedBook == null) {
            addedBook = book;
        }
        addedBook.getAuthors().add(author);
        bookRepository.save(addedBook);
        author.getBooks().add(addedBook);
        authorRepository.save(author);
    }


}