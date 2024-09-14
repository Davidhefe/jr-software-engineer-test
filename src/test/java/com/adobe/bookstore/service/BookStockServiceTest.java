package com.adobe.bookstore.service;

import com.adobe.bookstore.DO.OrderItemDO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.service.impl.BookStockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookStockServiceTest {

    @InjectMocks
    private BookStockService sut;

    @Mock
    private BookStockRepository bookStockRepository;

    @Test
    public void testExistsEnoughQuantityInStock_shouldReturnFalse_whenNotEnoughStockForAtLeastOneItem() {
        //Given
        List<OrderItemDO> orderItemList = new ArrayList<>();
        orderItemList.add(OrderItemDO.builder().bookId("12345-67890").quantity(10).build());
        BookStock bookStock = BookStock.builder().id("12345-67890").name("Hello").quantity(5).build();

        //When
        when(sut.getStockById("12345-67890")).thenReturn(Optional.of(bookStock));
        boolean result = sut.existsEnoughQuantityInStock(orderItemList);

        //Then
        assertFalse(result);
    }

    @Test
    public void testExistsEnoughQuantityInStock_shouldReturnTrue_whenEnoughStockForAllItems() {
        //Given
        List<OrderItemDO> orderItemList = new ArrayList<>();
        orderItemList.add(OrderItemDO.builder().bookId("12345-67890").quantity(1).build());
        BookStock bookStock = BookStock.builder().id("12345-67890").name("Hello").quantity(5).build();

        //When
        when(sut.getStockById("12345-67890")).thenReturn(Optional.of(bookStock));
        boolean result = sut.existsEnoughQuantityInStock(orderItemList);

        //Then
        assertTrue(result);
    }

    @Test
    public void testUpdateStock_shouldThrowRuntimeException_whenTheBookIdHasNotBeenFound() {
        //Given
        List<OrderItemDO> orderItemList = new ArrayList<>();
        orderItemList.add(OrderItemDO.builder().bookId("12345-67890").quantity(1).build());

        //When
        when(bookStockRepository.findById("12345-67890")).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sut.updateStock(orderItemList));

        //Then
        assertEquals("Book with ID 12345-67890 not found. " +
                "There was an error checking if existed enough stock.", thrown.getMessage());
    }

    @Test
    public void testUpdateStock_shouldThrowRuntimeException_whenNotEnoughStock() {
        //Given
        BookStock bookStock = BookStock.builder().id("12345-67890").name("Hello").quantity(5).build();
        List<OrderItemDO> orderItemList = new ArrayList<>();
        orderItemList.add(OrderItemDO.builder().bookId("12345-67890").quantity(10).build());

        //When
        when(bookStockRepository.findById("12345-67890")).thenReturn(Optional.of(bookStock));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sut.updateStock(orderItemList));

        //Then
        assertEquals("Not enough stock for book with ID 12345-67890. There was an error checking " +
                "if existed enough stock.", thrown.getMessage());
    }

    @Test
    public void testUpdateStock_shouldUpdateStock_whenCalled() {
        //Given
        BookStock bookStock1 = BookStock.builder().id("12345-67890").name("Hello").quantity(10).build();
        BookStock bookStock2 = BookStock.builder().id("54321-09876").name("book2").quantity(10).build();

        List<OrderItemDO> orderItemList = new ArrayList<>();
        orderItemList.add(OrderItemDO.builder().bookId("12345-67890").quantity(5).build());
        orderItemList.add(OrderItemDO.builder().bookId("54321-09876").quantity(5).build());

        //When
        when(bookStockRepository.findById("12345-67890")).thenReturn(Optional.of(bookStock1));
        when(bookStockRepository.findById("54321-09876")).thenReturn(Optional.of(bookStock2));

        sut.updateStock(orderItemList);

        //Then
        ArgumentCaptor<BookStock> bookStockCaptor = ArgumentCaptor.forClass(BookStock.class);

        verify(bookStockRepository, times(2)).save(bookStockCaptor.capture());
        List<BookStock> savedBookStocks = bookStockCaptor.getAllValues();

        assertTrue(savedBookStocks.stream().anyMatch(item -> "12345-67890".equals(item.getId())
                && item.getQuantity().equals(5)));
        assertTrue(savedBookStocks.stream().anyMatch(item -> "54321-09876".equals(item.getId())
                && item.getQuantity().equals(5)));
    }

    @Test
    public void testGetStockById_shouldReturnEmpty_whenBookDoesNotExist() {
        //When
        when(bookStockRepository.findById(anyString())).thenReturn(Optional.empty());
        Optional<BookStock> result = sut.getStockById("1234");

        //Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetStockById_shouldReturnBookStock_whenBookExists() {
        //Given
        BookStock bookStock = BookStock.builder().id("12345-67890").name("some book").quantity(7).build();

        //When
        when(bookStockRepository.findById("12345-67890")).thenReturn(Optional.of(bookStock));
        Optional<BookStock> result = sut.getStockById("12345-67890");

        //Then
        assertTrue(result.isPresent());
        assertEquals(7, result.get().getQuantity());
    }

}
