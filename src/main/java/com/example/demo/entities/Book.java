package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "books")
@EqualsAndHashCode(exclude = "authors")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", unique = true, nullable = false)
    @NotBlank
    private String title;

    @Column(name = "description")
    @NotBlank
    private String description;

    @Column(name = "number_pages")
    @Min(1)
    private int numberPages;

    @Column(name = "date_created")
    @PastOrPresent
    private LocalDate dateCreated;
    
    @Column(name = "price", precision = 10, scale = 2)
    @Digits(integer = 10, fraction = 2)
    @DecimalMin("0.01")
    private BigDecimal price;

    public Book() {
        authors = new HashSet<>();
    }

    @ManyToMany(mappedBy = "books")
    @JsonIgnoreProperties("books")
    @ToString.Exclude
    private Set<Author> authors;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Discount> discounts;
}