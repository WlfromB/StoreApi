package com.example.demo.service.author;

import com.example.demo.cache.PageDeserializer;
import com.example.demo.constant.classes.NotFoundConstants;
import com.example.demo.constant.classes.RolesConstants;
import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.AuthorDto;
import com.example.demo.dto.BookDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.service.cache.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CacheService cacheService;

    @Override
    @Transactional
    public Author getAuthorById(long id) throws Exception {
        String key = getKey(id);
        Author author = cacheService.getFromCache(key, new TypeReference<Author>() {
        });
        if (author == null) {
            author = authorRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(NotFoundConstants.AUTHOR));
            cacheService.setToCache(key, author, ttl);
        }
        return author;
    }

    @Override
    public Page<Author> getAllAuthors(Pageable pageable) throws Exception {
        String key = getKey(pageable);
        Page<Author> authors = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Author>>() {
                });
        if (authors == null) {
            authors = authorRepository.findAll(pageable);
            if (authors.isEmpty()) {
                throw new NotFoundException(NotFoundConstants.setMany(NotFoundConstants.AUTHOR));
            }
            cacheService.setToCache(key, authors, ttl);
        }
        return authors;
    }

    @Override
    @Transactional
    public Author saveAuthor(AuthorDto author, long userId) throws NotFoundException, IllegalArgumentException {
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException(NotFoundConstants.USER));
        Author authorEntity = authorRepository.save(author.from());
        if (user != null) {
            user.setAuthor(authorEntity);
            Role role = roleRepository.findByName(RolesConstants.AUTHOR)
                    .orElseThrow(() -> new NotFoundException(NotFoundConstants.ROLE));
            user.getRoles().add(role);
            role.getUsers().add(user);
            roleRepository.save(role);
            userRepository.save(user);
            return authorEntity;
        }
        throw new IllegalArgumentException(NotFoundConstants.USER);
    }

    @Override
    @Transactional
    public void addBook(BookDto book, long authorId) throws Exception {
        Author author = getAuthorById(authorId);
        Book addedBook = bookRepository.findBookByTitle(book.getTitle())
                .orElse(book.from());
        addedBook.getAuthors().add(author);
        author.getBooks().add(addedBook);
        authorRepository.save(author);
    }

    private String getKey(Long authorId) {
        return "author:%d".formatted(authorId);
    }

    private String getKey(Pageable pageable) {
        return "authors:%d:%d"
                .formatted(pageable.getPageNumber(), pageable.getPageSize());
    }
}