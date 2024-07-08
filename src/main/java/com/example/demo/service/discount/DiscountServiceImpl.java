package com.example.demo.service.discount;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dao.BookRepository;
import com.example.demo.dao.DiscountRepository;
import com.example.demo.dto.DiscountDto;
import com.example.demo.entities.Discount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final static Supplier<Exception> NOT_FOUND_EXCEPTION_SUPPLIER = () -> new Exception("Not Found");
    private final DiscountRepository discountRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public Page<Discount> getDiscountsByBook(Long bookId, Pageable pageable) throws Exception {
        return discountRepository.findDiscountsByBook(
                bookRepository.findById(bookId).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER),
                pageable);
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountByBookAndDate(Long bookId, LocalDate date, Pageable pageable)
            throws Exception {
        return discountRepository
                .findDiscountByBookAndDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
                        (bookRepository.findById(bookId).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER),
                                date, date, pageable);
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountByAuthor(Long authorId, Pageable pageable) throws Exception {
        return discountRepository
                .findDiscountByAuthor(authorRepository.findById(authorId)
                        .orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER), pageable);
    }

    @Override
    @Transactional
    public Page<Discount> getDiscountsByDate(LocalDate date, Pageable pageable) throws Exception {
        return discountRepository
                .findDiscountByDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual(
                        date, date, pageable);
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
    public Page<Discount> getAll(Pageable pageable) {
        return discountRepository.findAll(pageable);
    }
}
