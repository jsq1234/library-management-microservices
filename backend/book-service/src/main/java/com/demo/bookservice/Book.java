package com.demo.bookservice;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @Positive(message = "Price must be positive")
    private Float price;

    @PositiveOrZero(message = "Quantity must be non-negative")
    private int quantity;

    @NotBlank(message = "Author cannot be blank")
    @Column(nullable = false)
    private String author;

    @Pattern(regexp = "\\d{3}-\\d{10}", message = "Invalid ISBN format")
    @Column(nullable = false)
    private String ISBN;

    @NotBlank(message = "Genre cannot be blank")
    @Column(nullable = false)
    private String genre;

    @Past(message = "Publish date must be in the past")
    @Column(nullable = false)
    private LocalDate publishDate;
}

