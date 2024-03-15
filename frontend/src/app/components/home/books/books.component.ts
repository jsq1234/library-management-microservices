import { Component, OnInit } from '@angular/core';
import { Book } from 'src/app/interfaces/book';
import { BookService } from 'src/app/services/book.service';

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.css'],
})
export class BooksComponent implements OnInit {
  constructor(private bookService: BookService) {}
  public books: Book[] | null = null;

  ngOnInit(): void {
    this.bookService.getAllBooks().subscribe({
      next: (value) => {
        this.books = value;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }
}
