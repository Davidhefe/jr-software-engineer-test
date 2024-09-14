package com.adobe.bookstore.service;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.model.BookStock;

import java.util.List;
import java.util.Optional;

public interface IBookStockService {

    boolean existsEnoughQuantityInStock(List<OrderItemDO> order);

    void updateStock(List<OrderItemDO> order);

    Optional<BookStock> getStockById(String bookId);

}
