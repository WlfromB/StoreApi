package com.example.demo.requests;

import com.example.demo.entities.Author;
import lombok.Data;

import java.util.Set;

@Data
public class AddBookToAuthor {
    Author author;
    Set<Long> booksIds;
}
