package com.adobe.bookstore.service.impl;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.service.IBookStockService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookStockService implements IBookStockService {

    private BookStockRepository bookStockRepository;

    public BookStockService(BookStockRepository bookStockRepository) {
        this.bookStockRepository = bookStockRepository;
    }

    @Override
    public boolean existsEnoughQuantityInStock(List<OrderItemDO> order) {
        return order.stream()
                .allMatch(orderItem ->
                        this.getStockById(orderItem.getBookId())
                                .map(BookStock::getQuantity)
                                .filter(quantityStock -> quantityStock >= orderItem.getQuantity())
                                .isPresent()
                );

        /* Imperative programming version
        for (OrderItemDO orderItem: order) {
            String orderItemId = orderItem.getBookId();
            Integer orderItemQuantity = orderItem.getQuantity();

            Optional<BookStock> itemStock = this.getStockById(orderItemId);

            if (!itemStock.isPresent() || itemStock.get().getQuantity() < orderItemQuantity) {
                return false;
            }
        }

        return true;
        */
    }

    @Override
    public void updateStock(List<OrderItemDO> order) {
        order.forEach(orderItem -> bookStockRepository.findById(orderItem.getBookId())
                    .ifPresentOrElse(bookStock -> {
                        int newQuantity = bookStock.getQuantity() - orderItem.getQuantity();
                        if (newQuantity < 0) {
                            throw new RuntimeException("Not enough stock for book with ID " + orderItem.getBookId()
                                    + ". There was an error checking if existed enough stock.");
                        }
                        bookStock.setQuantity(newQuantity);
                        bookStockRepository.save(bookStock);
                    },
                    () -> {
                        throw new RuntimeException("Book with ID " + orderItem.getBookId()
                                + " not found. There was an error checking if existed enough stock.");
                    })
                );

        /* Imperative programming version
        for (OrderItemDO orderItem : order) {
        Optional<BookStock> bookStockOptional = bookStockRepository.findById(orderItem.getBookId());
        if (bookStockOptional.isEmpty()) {
            throw new RuntimeException("Book with ID " + orderItem.getBookId()
                    + " not found. There was an error checking if existed enough stock.");
        }

        BookStock bookStock = bookStockOptional.get();
        int newQuantity = bookStock.getQuantity() - orderItem.getQuantity();
        if (newQuantity < 0) {
            throw new RuntimeException("Not enough stock for book with ID " + orderItem.getBookId()
                    + ". There was an error checking if existed enough stock.");
        }

        bookStock.setQuantity(newQuantity);
        bookStockRepository.save(bookStock);
         */

    }

    @Override
    public Optional<BookStock> getStockById(String bookId) {
        return bookStockRepository.findById(bookId);
    }

}
