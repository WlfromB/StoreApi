package com.example.demo.service.discount;

import com.example.demo.cache.PageDeserializer;
import com.example.demo.constant.classes.NotFoundConstants;
import com.example.demo.dao.DiscountRepository;
import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.example.demo.entities.Discount;
import com.example.demo.service.author.AuthorService;
import com.example.demo.service.book.BookService;
import com.example.demo.service.cache.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final CacheService cacheService;
    private final BookService bookService;
    private final AuthorService authorService;

    @Override
    @Transactional
    public Page<Discount> getDiscountsByBook(Long bookId, Pageable pageable) throws Exception {
        String key = getKey(Book.class.getName(), bookId, pageable);
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository.findDiscountsByBook(
                    bookService.getBookById(bookId),
                    pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException(NotFoundConstants.setMany(NotFoundConstants.DISCOUNT));
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date, Pageable pageable)
            throws Exception {
        String key = getKey(bookId, date, pageable);
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository
                    .findDiscountByBookAndDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
                            (bookService.getBookById(bookId), date, date, pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException(NotFoundConstants.setMany(NotFoundConstants.DISCOUNT));
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountByAuthor(Long authorId, Pageable pageable) throws Exception {
        String key = getKey(Author.class.getName(), authorId, pageable);
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository
                    .findDiscountByAuthor(authorService.getAuthorById(authorId), pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException(NotFoundConstants.setMany(NotFoundConstants.DISCOUNT));
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountsByDate(LocalDate date, Pageable pageable) throws Exception {
        String key = getKey(date, pageable);
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository
                    .findDiscountByDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual(
                            date, date, pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException(NotFoundConstants.setMany(NotFoundConstants.DISCOUNT));
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Discount saveDiscount(DiscountDto discount) throws Exception {
        return discountRepository
                .save(discount
                        .from(bookService.getBookById(discount.getBookId())));
    }

    @Override
    @Transactional
    public Page<Discount> getAll(Pageable pageable) throws Exception {
        String key = getKey(pageable);
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository.findAll(pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException(NotFoundConstants.setMany(NotFoundConstants.DISCOUNT));
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }


}
