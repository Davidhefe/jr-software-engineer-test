package com.adobe.bookstore.service;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.model.BookOrderItem;
import com.adobe.bookstore.repository.BookOrderRepository;
import com.adobe.bookstore.service.impl.BookOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookOrderServiceTest {

    @InjectMocks
    private BookOrderService sut;

    @Mock
    private BookOrderRepository bookOrderRepository;

    @Test
    public void testCreateOrder_shouldSaveAllItems_whenCalled() {
        //Given
        List<OrderItemDO> order = new ArrayList<>();
        order.add(OrderItemDO.builder().bookId("abcde").quantity(8).build());
        order.add(OrderItemDO.builder().bookId("12345").quantity(4).build());

        //When
        String orderId = sut.createOrder(order);

        //Then
        ArgumentCaptor<BookOrderItem> captor = ArgumentCaptor.forClass(BookOrderItem.class);
        verify(bookOrderRepository, times(2)).save(captor.capture());

        List<BookOrderItem> savedItems = captor.getAllValues();
        assertTrue(savedItems.stream().anyMatch(item -> "abcde".equals(item.getBookId())
                && item.getOrderId().equals(orderId) && item.getQuantity().equals(8)));
        assertTrue(savedItems.stream().anyMatch(item -> "12345".equals(item.getBookId())
                && item.getOrderId().equals(orderId) && item.getQuantity().equals(4)));
    }

    @Test
    public void testGetOrders_shouldThrowException_whenErrorRetrievingOrders() {
        //When
        when(bookOrderRepository.findAll()).thenThrow(new RuntimeException("There was an error retrieving the orders."));
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> sut.getOrders());

        //Then
        assertEquals("There was an error retrieving the orders.", thrownException.getMessage());
    }

    @Test
    public void testGetOrders_shouldReturnAnEmptyMap_whenNoOrdersHaveBeenDone() {
        //Given
        List<BookOrderItem> expectedOrderItemsList = new ArrayList<>();

        //When
        when(bookOrderRepository.findAll()).thenReturn(expectedOrderItemsList);
        Map<String, List<OrderItemDO>> result = sut.getOrders(); // Replace with your actual method

        //Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetOrders_shouldReturnAllOrders_whenCalled() {
        //Given
        List<BookOrderItem> expectedOrderItemsList = createOrderList();
        Map<String, List<OrderItemDO>> expectedOrderMap = createMapFromOrderItemsList(expectedOrderItemsList);

        //When
        when(bookOrderRepository.findAll()).thenReturn(expectedOrderItemsList);
        Map<String, List<OrderItemDO>> result = sut.getOrders(); // Replace with your actual method

        //Then
        assertEquals(expectedOrderMap, result);
    }

    private Map<String, List<OrderItemDO>> createMapFromOrderItemsList(List<BookOrderItem> bookOrderItemList) {
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

    private List<BookOrderItem> createOrderList() {
        List<BookOrderItem> expectedOrderList = new ArrayList<>();
        expectedOrderList.add(BookOrderItem.builder().orderId("12345").bookId("AAA").quantity(1).build());
        expectedOrderList.add(BookOrderItem.builder().orderId("12345").bookId("AAA").quantity(1).build());
        expectedOrderList.add(BookOrderItem.builder().orderId("12345").bookId("AAA").quantity(1).build());
        return expectedOrderList;
    }

}
