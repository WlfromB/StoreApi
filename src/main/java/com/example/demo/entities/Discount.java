package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "discounts")
public class Discount {
    private final static String titleForAll = "_all";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Set<Author> authors;

    @Column(name = "date_of_sale_start")
    @FutureOrPresent
    private LocalDate dateOfSaleStart;

    
    @Column(name = "date_of_sale_end")
    @Future
    private LocalDate dateOfSaleEnd;

    @Column(name = "dimension_of_sale", precision = 4, scale = 2)
    @DecimalMax("99.9")
    @DecimalMin("0.01")
    @Digits(integer = 2, fraction = 2)    
    private BigDecimal dimensionOfSale;
    
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;
    
    public Discount(){
        authors = new HashSet<>();
    }
}
