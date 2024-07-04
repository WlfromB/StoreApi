package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(name = "date_of_sale_start")
    private LocalDate dateOfSaleStart;

    
    @Column(name = "date_of_sale_end")
    private LocalDate dateOfSaleEnd;

    @Column(name = "dimension_of_sale", precision = 4, scale = 2)
    private BigDecimal dimensionOfSale;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;
    
    public Discount(){
    }
}
