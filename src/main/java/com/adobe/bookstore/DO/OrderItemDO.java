package com.adobe.bookstore.DO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDO {
    private String bookId;
    private Integer quantity;
}
