package com.adobe.bookstore.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item", uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "book_id"})})
@JsonSerialize
@IdClass(BookOrderItemId.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookOrderItem {

    @Id
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Id
    @Column(name = "book_id", nullable = false)
    private String bookId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}
