package com.example.demo.service.discount;

import com.example.demo.cache.PageDeserializer;
import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dao.DiscountRepository;
import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import com.example.demo.service.cache.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final static Supplier<Exception> NOT_FOUND_EXCEPTION_SUPPLIER = () -> new IllegalArgumentException("Not Found");
    private final DiscountRepository discountRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CacheService cacheService;
    private final int ttl = 900;

    @Override
    @Transactional
    public Page<Discount> getDiscountsByBook(Long bookId, Pageable pageable) throws Exception {
        String key = "discountByBook:%d:%d:%d"
                .formatted(bookId, pageable.getPageNumber(), pageable.getPageSize());
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository.findDiscountsByBook(
                    bookRepository.findById(bookId).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER),
                    pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException("No discounts found");
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date, Pageable pageable)
            throws Exception {
        String key = "discountByBookAndDate:%d:%d:%d:%d"
                .formatted(bookId, date, pageable.getPageNumber(), pageable.getPageSize());
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository
                    .findDiscountByBookAndDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
                            (bookRepository.findById(bookId).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER),
                                    date, date, pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException("No discounts found");
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountByAuthor(Long authorId, Pageable pageable) throws Exception {
        String key = "discountByAuthor:%d:%d:%d"
                .formatted(authorId, pageable.getPageNumber(), pageable.getPageSize());
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository
                    .findDiscountByAuthor(authorRepository.findById(authorId)
                            .orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER), pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException("No discounts found");
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountsByDate(LocalDate date, Pageable pageable) throws Exception {
        String key = "discountByDate:%d:%d:%d"
                .formatted(date, pageable.getPageNumber(), pageable.getPageSize());
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository
                    .findDiscountByDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual(
                            date, date, pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException("No discounts found");
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
                        .from(bookRepository.findById(discount.getBookId())
                                .orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER)));
    }

    @Override
    @Transactional
    public Page<Discount> getAll(Pageable pageable) throws Exception {
        String key = "discount:%d:%d"
                .formatted(pageable.getPageNumber(), pageable.getPageSize());
        PageDeserializer<Discount> discounts = cacheService
                .getFromCache(key, new TypeReference<PageDeserializer<Discount>>() {
                });
        if (discounts == null) {
            discounts = new PageDeserializer<>(discountRepository.findAll(pageable));
            if (discounts.isEmpty()) {
                throw new IllegalArgumentException("No discounts found");
            }
            cacheService.setToCache(key, discounts, ttl);
        }
        return discounts;
    }
}
