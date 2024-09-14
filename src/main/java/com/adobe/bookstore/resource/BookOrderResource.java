package com.adobe.bookstore.resource;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.service.IBookOrderService;
import com.adobe.bookstore.service.IBookStockService;
import com.adobe.bookstore.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/orders/")
public class BookOrderResource {

    private IBookOrderService bookOrderService;
    private IBookStockService bookStockService;

    @Autowired
    public BookOrderResource(IBookOrderService bookOrderService, IBookStockService bookStockService) {
        this.bookOrderService = bookOrderService;
        this.bookStockService = bookStockService;
    }

    @PostMapping("create")
    public ResponseEntity<String> createOrder(@RequestBody List<OrderItemDO> order) {
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The order has been rejected because " +
                    "it was empty.");
        }

        try {
            if (!bookStockService.existsEnoughQuantityInStock(order)) {
                Log.info("Not enough stock.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The order has been rejected because " +
                        "at least one item does not have the required quantity available on stock.");
            }
        } catch (Exception e) {
            Log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred " +
                    "checking if there was enough stock.");
        }

        String orderId;
        try {
            orderId = bookOrderService.createOrder(order);
            Log.info("Order created successfully.");
        } catch (Exception e) {
            Log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred " +
                    "creating the Order.");
        }

        CompletableFuture.runAsync(() -> {
            try {
                bookStockService.updateStock(order);
                Log.info("Stock updated successfully.");
            } catch (Exception e) {
                Log.error(e.getMessage());
            }
        });

        return ResponseEntity.ok("Order created successfully. Order ID: " + orderId);
    }

    @GetMapping("")
    public ResponseEntity<?> getOrders() {
        try {
            return ResponseEntity.ok(bookOrderService.getOrders());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
