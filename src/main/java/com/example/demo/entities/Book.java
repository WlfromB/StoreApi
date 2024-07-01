package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "number_pages")
    private int numberPages;

    @Column(name = "date_created")
    private Date dateCreated;

    public Book() {
        authors = new HashSet<>();
    }

    @ManyToMany(mappedBy = "books")
    @JsonIgnoreProperties("books")
    @ToString.Exclude
    private Set<Author> authors;
}