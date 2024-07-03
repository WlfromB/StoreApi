package com.example.demo.service.author;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.entities.Author;

import com.example.demo.entities.Book;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Autowired
    private UserRepository userRepository;

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
    public Page<Author> getAllAuthors(Pageable pageable) throws Exception {
        Page<Author> authors = authorRepository.findAll(pageable);
        if (authors.isEmpty()) {
            throw new Exception("Authors not found");
        }
        return authors;
    }

    @Override
    @Transactional
    public void saveAuthor(Author author, long userId) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        if(user!=null){
            user.setAuthor(author);
            user.changeRole(Role.Author);
            userRepository.save(user);
            authorRepository.save(author);
            return;
        }        
        throw new Exception("User not found");
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
        author.getBooks().add(addedBook);
        authorRepository.save(author);
    }


}