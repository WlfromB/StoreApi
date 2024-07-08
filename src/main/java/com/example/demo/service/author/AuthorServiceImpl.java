package com.example.demo.service.author;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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
    public Author saveAuthor(AuthorDto author, long userId) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        Author authorEntity = author.from();
        if (user != null) {
            user.setAuthor(authorEntity);
            user.getRole().add(Role.Author);
            userRepository.save(user);
            return authorRepository.save(authorEntity);
        }
        throw new Exception("User not found");
    }

    @Override
    @Transactional
    public void addBook(BookDto book, long authorId) throws Exception {
        Author author = getAuthorById(authorId);
        Book addedBook = bookRepository.findBookByTitle(book.getTitle());
        if (addedBook == null) {
            addedBook = book.from();
        }
        addedBook.getAuthors().add(author);
        author.getBooks().add(addedBook);
        authorRepository.save(author);
    }


}