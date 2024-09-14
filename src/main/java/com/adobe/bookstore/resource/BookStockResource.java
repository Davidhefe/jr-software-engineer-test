package com.adobe.bookstore.resource;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.service.IBookStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books_stock/")
public class BookStockResource {

    private IBookStockService bookStockService;

    @Autowired
    public BookStockResource(IBookStockService bookStockService) {
        this.bookStockService = bookStockService;
    }

    @GetMapping("{bookId}")
    public ResponseEntity<BookStock> getStockById(@PathVariable String bookId) {
        return bookStockService.getStockById(bookId)
                .map(bookStock -> ResponseEntity.ok(bookStock))
                .orElse(ResponseEntity.notFound().build());
    }

}
