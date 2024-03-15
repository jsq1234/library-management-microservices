package com.demo.bookservice;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public void createBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Book book) {
        bookRepository.deleteById(book.getId());
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByQuantityGreaterThan(0);
    }

    public List<Book> searchBooks(String title) {
        if (title == null || title.length() == 0) {
            return List.of();
        }

        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

}


