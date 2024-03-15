package com.demo.bookservice;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public ResponseEntity<List<Book>> getAllBooks(@RequestHeader(name = "role") String role) {
        AuthorizationUtil.userCheck(role);
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping()
    public ResponseEntity<Book> addBook(@RequestHeader(name = "role") String role,
            @Valid @RequestBody Book book) {
        AuthorizationUtil.adminCheck(role);
        bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}
