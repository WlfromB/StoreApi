package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "authors")
@EqualsAndHashCode(exclude = "books")
@Schema(description = "Сущность автора")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "firstname")
    @NotBlank
    private String firstName;

    @Column(name = "lastname")
    @NotBlank
    private String lastName;

    @Column(nullable = false, name = "day_of_birthday")
    @Past
    private LocalDate dateOfBirthday;

    public Author() {
        books = new HashSet<>();
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("authors")
    @JoinTable(
            name = "authors_books",
            joinColumns = {@JoinColumn(name = "author_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")}
    )
    @ToString.Exclude
    private Set<Book> books;
    
}
