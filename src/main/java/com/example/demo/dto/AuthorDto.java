package com.example.demo.dto;

import com.example.demo.entities.Author;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Класс для добавления автора")
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
    
    public AuthorDto (){}
    
    public AuthorDto(Author author){
        this.firstName = author.getFirstName();
        this.lastName = author.getLastName();
        this.dateOfBirthday = author.getDateOfBirthday();
    }
}
