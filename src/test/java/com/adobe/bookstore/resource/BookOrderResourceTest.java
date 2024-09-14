package com.adobe.bookstore.resource;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.service.IBookOrderService;
import com.adobe.bookstore.service.IBookStockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookOrderResourceTest {

    @InjectMocks
    private BookOrderResource sut;

    @Mock
    private IBookOrderService bookOrderService;

    @Mock
    private IBookStockService bookStockService;

    @Test
    public void testCreateOrder_shouldReturnBadRequest_whenOrderIsEmpty() {
        //Given
        List<OrderItemDO> order = new ArrayList<>();

        //When
        ResponseEntity<String> response = sut.createOrder(order);

        //Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The order has been rejected because it was empty.", response.getBody());
    }

    @Test
    public void testCreateOrder_shouldReturnInternalServerError_whenUnknownErrorCheckingEnoughQuantityInStockForThatOrder() {
        //Given
        List<OrderItemDO> order = new ArrayList<>();
        order.add(OrderItemDO.builder().bookId("abcde").quantity(5).build());

        //When
        when(bookStockService.existsEnoughQuantityInStock(order)).thenThrow(new RuntimeException());
        ResponseEntity<String> response = sut.createOrder(order);

        //Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred checking if there was enough stock.", response.getBody());
    }

    @Test
    public void testCreateOrder_shouldReturnBadRequest_whenNotEnoughQuantityInStockForThatOrder() {
        //Given
        List<OrderItemDO> order = new ArrayList<>();
        order.add(OrderItemDO.builder().bookId("abcde").quantity(5).build());

        //When
        when(bookStockService.existsEnoughQuantityInStock(order)).thenReturn(false);
        ResponseEntity<String> response = sut.createOrder(order);

        //Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The order has been rejected because at least one item does not have " +
                "the required quantity available on stock.", response.getBody());
    }

    @Test
    public void testCreateOrder_shouldReturnInternalServerError_whenUnknownErrorCreatingOrder() {
        //Given
        List<OrderItemDO> order = new ArrayList<>();
        order.add(OrderItemDO.builder().bookId("abcde").quantity(5).build());

        //When
        when(bookStockService.existsEnoughQuantityInStock(order)).thenReturn(true);
        when(bookOrderService.createOrder(order)).thenThrow(new RuntimeException());
        ResponseEntity<String> response = sut.createOrder(order);

        //Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred creating the Order.", response.getBody());
    }

    @Test
    public void testCreateOrder_shouldReturnOK_whenErrorUpdatingStock() {
        //Given
        String orderId = "123456";
        String bookId = "abcde";
        List<OrderItemDO> order = new ArrayList<>();
        order.add(OrderItemDO.builder().bookId(bookId).quantity(5).build());

        //When
        when(bookStockService.existsEnoughQuantityInStock(order)).thenReturn(true);
        when(bookOrderService.createOrder(order)).thenReturn(orderId);
        doThrow(new RuntimeException("Book with ID " + bookId + " not found. There was an error checking " +
                "if existed enough stock.")).when(bookStockService).updateStock(order);
        ResponseEntity<String> response = sut.createOrder(order);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order created successfully. Order ID: " + orderId, response.getBody());

        CompletableFuture.runAsync(() -> {}).join();
    }

    @Test
    public void testCreateOrder_shouldReturnOK_whenCalled() {
        //Given
        String orderId = "123456";
        String bookId = "abcde";
        List<OrderItemDO> order = new ArrayList<>();
        order.add(OrderItemDO.builder().bookId(bookId).quantity(0).build());

        //When
        when(bookStockService.existsEnoughQuantityInStock(order)).thenReturn(true);
        when(bookOrderService.createOrder(order)).thenReturn(orderId);
        ResponseEntity<String> response = sut.createOrder(order);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order created successfully. Order ID: " + orderId, response.getBody());
    }

    @Test
    public void testGetOrders_shouldReturnListWithCreatedOrders_whenCalled() {
        //Given
        Map<String, List<OrderItemDO>> bookOrderItemList = new HashMap<>();
        List<OrderItemDO> orderItemList = new ArrayList<>();
        orderItemList.add(OrderItemDO.builder().bookId("12345-67890").quantity(5).build());
        bookOrderItemList.put("12345-67890-abcde", orderItemList);

        //When
        when(bookOrderService.getOrders()).thenReturn(bookOrderItemList);
        ResponseEntity<?> response = sut.getOrders();

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookOrderItemList, response.getBody());
    }

    @Test
    public void testGetOrders_shouldReturnEmptyList_whenCalled() {
        //Given
        Map<String, List<OrderItemDO>> bookOrderItemList = new HashMap<>();

        //When
        when(bookOrderService.getOrders()).thenReturn(bookOrderItemList);
        ResponseEntity<?> response = sut.getOrders();

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookOrderItemList, response.getBody());
    }

    @Test
    public void testGetOrders_shouldReturnError_whenCalled() {
        //When
        when(bookOrderService.getOrders()).thenThrow(new RuntimeException("There was an error retrieving the orders."));
        ResponseEntity<?> response = sut.getOrders();

        //Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("There was an error retrieving the orders.", response.getBody());
    }

}
