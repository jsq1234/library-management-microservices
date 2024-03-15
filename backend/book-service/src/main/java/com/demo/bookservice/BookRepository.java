package com.demo.bookservice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByQuantityGreaterThan(int quantity);

    List<Book> findByTitleContainingIgnoreCase(String title);
}
