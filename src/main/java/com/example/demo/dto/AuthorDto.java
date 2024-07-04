package com.example.demo.dto;

import com.example.demo.entities.Author;
import com.example.demo.entities.Book;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class AuthorDto {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @Past
    private LocalDate dateOfBirthday;
    
    public Author from(){
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setDateOfBirthday(dateOfBirthday);
        return author;
    }
}
