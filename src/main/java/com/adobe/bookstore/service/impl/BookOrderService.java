package com.adobe.bookstore.service.impl;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.model.BookOrderItem;
import com.adobe.bookstore.repository.BookOrderRepository;
import com.adobe.bookstore.service.IBookOrderService;
import com.adobe.bookstore.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BookOrderService implements IBookOrderService {

    private BookOrderRepository bookOrderRepository;

    @Autowired
    public BookOrderService(BookOrderRepository bookOrderRepository) {
        this.bookOrderRepository = bookOrderRepository;
    }

    @Override
    @Transactional
    public String createOrder(List<OrderItemDO> order) {
        String orderId = UUID.randomUUID().toString();

        order.stream()
                .map(orderItem -> BookOrderItem.builder()
                        .orderId(orderId)
                        .bookId(orderItem.getBookId())
                        .quantity(orderItem.getQuantity())
                        .build())
                .forEach(bookOrderRepository::save);

        return orderId;

        /* Imperative programming version
        for (OrderItemDO orderItem : order) {

            BookOrderItem bookOrderItem = new BookOrderItem.Builder()
                    .orderId(orderId)
                    .quantity(orderItem.getQuantity())
                    .build();

            bookOrderRepository.save(bookOrderItem);
        }

        return orderId;
         */
    }

    @Override
    public Map<String, List<OrderItemDO>> getOrders() {
        List<BookOrderItem> bookOrderItemList;
        try {
            bookOrderItemList = bookOrderRepository.findAll();
            Log.info("Orders retrieved successfully.");
        } catch (Exception e) {
            Log.error("There was an error retrieving the orders.");
            throw new RuntimeException("There was an error retrieving the orders.");
        }

        return createMapFromBookOrderItemsList(bookOrderItemList);
    }

    private Map<String, List<OrderItemDO>> createMapFromBookOrderItemsList(List<BookOrderItem> bookOrderItemList) {
        Map<String, List<OrderItemDO>> bookOrderMap = new HashMap<>();

        for (BookOrderItem bookOrderItem : bookOrderItemList) {
            String orderId = bookOrderItem.getOrderId();

            if (!bookOrderMap.containsKey(orderId)) {
                bookOrderMap.put(orderId, new ArrayList<>());
            }

            OrderItemDO orderItem = OrderItemDO.builder()
                                    .bookId(bookOrderItem.getBookId())
                                    .quantity(bookOrderItem.getQuantity())
                                    .build();

            bookOrderMap.get(orderId).add(orderItem);
        }

        return bookOrderMap;
    }

}
