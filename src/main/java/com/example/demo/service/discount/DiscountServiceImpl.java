package com.example.demo.service.discount;

import com.example.demo.dao.BookRepository;
import com.example.demo.dao.DiscountRepository;
import com.example.demo.entities.Author;
import com.example.demo.entities.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final static Supplier<Exception> NOT_FOUND_EXCEPTION_SUPPLIER = () -> new Exception("Not Found");
    
    @Autowired
    private DiscountRepository discountRepository;
    
    @Autowired
    private BookRepository bookRepository;    
    
    @Override
    @Transactional
    public Discount getDiscountForBook(Long bookId) throws Exception {
        return discountRepository.findDiscountByBook(
                bookRepository.findById(bookId).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER))
                .orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    @Override
    @Transactional
    public List<Discount> getDiscountForBookByDate(Long bookId, LocalDate date) throws Exception {
        return discountRepository
                .findDiscountByBookAndDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual
                        (bookRepository.findById(bookId).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER),
                                date, date).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER);
    } 

    @Override
    public List<Discount> getDiscountByAuthors(Set<Author> authors) throws Exception {
        return discountRepository
                .findDiscountByAuthors(authors).orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    @Override
    public List<Discount> getDiscountsByDate(LocalDate date) throws Exception {
        return discountRepository.findDiscountByDateOfSaleStartLessThanEqualAndDateOfSaleEndGreaterThanEqual(date, date)
                .orElseThrow(NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    @Override
    public Discount saveDiscounts(Discount discount) throws Exception {
        return discountRepository.save(discount);
    }
}
