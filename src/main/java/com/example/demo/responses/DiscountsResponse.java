package com.example.demo.responses;

import com.example.demo.entities.Book;
import lombok.Data;

@Data
public class DiscountsResponse {
    private Book book;
    private double discount;
}
