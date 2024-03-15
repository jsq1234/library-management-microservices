package com.demo.bookservice;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DummyBooksLoader {
    private final BookService bookService;
    @PostConstruct
    void init(){
        List.of(
                Book.builder()
                        .title("To Kill a Mockingbird")
                        .author("Harper Lee")
                        .price(19.99f)
                        .quantity(10)
                        .ISBN("978-0061120084")
                        .genre("Classic")
                        .publishDate(LocalDate.of(1960, 7, 11))
                        .build(),
                Book.builder()
                        .title("1984")
                        .author("George Orwell")
                        .price(15.50f)
                        .quantity(20)
                        .ISBN("978-0451524935")
                        .genre("Dystopian")
                        .publishDate(LocalDate.of(1949, 6, 8))
                        .build(),
                Book.builder()
                        .title("The Great Gatsby")
                        .author("F. Scott Fitzgerald")
                        .price(12.75f)
                        .quantity(15)
                        .ISBN("978-0743273565")
                        .genre("Literary Fiction")
                        .publishDate(LocalDate.of(1925, 4, 10))
                        .build(),
                Book.builder()
                        .title("Pride and Prejudice")
                        .author("Jane Austen")
                        .price(14.25f)
                        .quantity(18)
                        .ISBN("978-0486284736")
                        .genre("Romance")
                        .publishDate(LocalDate.of(1813, 1, 28))
                        .build(),
                Book.builder()
                        .title("The Catcher in the Rye")
                        .author("J.D. Salinger")
                        .price(17.99f)
                        .quantity(12)
                        .ISBN("978-0316769488")
                        .genre("Coming-of-age")
                        .publishDate(LocalDate.of(1951, 7, 16))
                        .build()
        ).forEach(bookService::createBook);
    }
}
