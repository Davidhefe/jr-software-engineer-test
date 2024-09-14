package com.adobe.bookstore.resource;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.service.IBookStockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class BookStockResourceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @InjectMocks
    private BookStockResource sut;

    @Mock
    private IBookStockService bookStockService;

    @Test
    public void testGetStockById_shouldReturnOk_whenStockExists() {
        //Given
        String bookId = "abc123";
        BookStock bookStock = BookStock.builder().id(bookId).name("The Great Gatsby").quantity(9).build();

        //When
        when(bookStockService.getStockById(bookId)).thenReturn(Optional.of(bookStock));
        ResponseEntity<BookStock> response = sut.getStockById(bookId);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookStock, response.getBody());
    }

    @Test
    public void testGetStockById_shouldReturnNotFound_whenStockDoesNotExist() {
        //Given
        String bookId = "abc123";

        //When
        when(bookStockService.getStockById(bookId)).thenReturn(Optional.empty());
        ResponseEntity<BookStock> response = sut.getStockById(bookId);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @Sql(statements = "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67890', 'some book', 7)")
    public void shouldReturnCurrentStock() {
        var result = restTemplate.getForObject("http://localhost:" + port + "/books_stock/12345-67890",
                BookStock.class);

        assertThat(result.getQuantity()).isEqualTo(7);
    }

    @Test
    public void shouldReturnNotFoundForEmptyStock() {
        ResponseEntity<BookStock> response = restTemplate.getForEntity("http://localhost:" + port +
                "/books_stock/12345-67890", BookStock.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
